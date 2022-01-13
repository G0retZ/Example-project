package com.cargopull.executor_driver.presentation.onlinebutton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OnlineButtonViewModel viewModel;
  private TestScheduler testScheduler;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase;
  @Mock
  private Observer<ViewState<OnlineButtonViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.never());
    viewModel = new OnlineButtonViewModelImpl(errorReporter, vehiclesAndOptionsUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportNetworkError() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.error(
        new HttpException(
            Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
        )
    ));

    // Action:
    viewModel.goOnline();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку сети.
   */
  @Test
  public void doNotReportNoNetworkError() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку блокировки.
   */
  @Test
  public void reportDriverBlockedError() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(errorReporter, only()).reportError(any(DriverBlockedException.class));
  }

  /**
   * Должен отправить ошибку отсуствствия доступных ТС.
   */
  @Test
  public void reportNoVehiclesAvailableError() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new EmptyListException()));

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(errorReporter, only()).reportError(any(EmptyListException.class));
  }

  /**
   * Должен отправить ошибку отсуствствия свободных ТС.
   */
  @Test
  public void reportNoFreeVehiclesError() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoSuchElementException()));

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(errorReporter, only()).reportError(any(NoSuchElementException.class));
  }

  /* Тетсируем работу с юзкейсом выхода на линию. */

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   */
  @Test
  public void DoNotTouchOnlineUseCaseUntilRequestFinished() {
    // Action:
    viewModel.goOnline();
    viewModel.goOnline();
    viewModel.goOnline();

    // Effect:
    verify(vehiclesAndOptionsUseCase, only()).loadVehiclesAndOptions();
  }

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   */
  @Test
  public void DoNotTouchOnlineUseCaseToGoOnlineUntilTimeout() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(vehiclesAndOptionsUseCase);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    viewModel.goOnline();
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    viewModel.goOnline();

    // Effect:
    inOrder.verify(vehiclesAndOptionsUseCase).loadVehiclesAndOptions();
    inOrder.verifyNoMoreInteractions();
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    viewModel.goOnline();
    inOrder.verify(vehiclesAndOptionsUseCase).loadVehiclesAndOptions();
    verifyNoMoreInteractions(vehiclesAndOptionsUseCase);

  }

  /**
   * Должен попросить юзкейс отправить выйти на линию.
   */
  @Test
  public void askOnlineUseCaseToGoOnline() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    verify(vehiclesAndOptionsUseCase, times(3)).loadVehiclesAndOptions();
    verifyNoMoreInteractions(vehiclesAndOptionsUseCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть рабочее состояние вида.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(OnlineButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" для запроса выхода на линию.
   */
  @Test
  public void setPendingViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие", если водитель заблокирован.
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForBlockedDriver() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если водитель заблокирован.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForBlockedDriver() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие", если нет ТС.
   */
  @Test
  public void setHoldPendingViewStateToLiveDataAfterFailForNoVehicles() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new EmptyListException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если нет ТС.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoVehicles() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new EmptyListException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие", если нет свободных ТС.
   */
  @Test
  public void setHoldPendingViewStateToLiveDataAfterFailForNoFreeVehicles() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoSuchElementException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние вида после таймера, если нет свободных ТС.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoFreeVehicles() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoSuchElementException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие", если нет сети.
   */
  @Test
  public void setHoldPendingViewStateToLiveDataForNoConnection() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние вида после таймера, если нет сети.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoConnection() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие", при другой ошибке.
   */
  @Test
  public void setHoldPendingViewStateToLiveDataAfterOtherFail() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new Exception()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние вида после таймера, при другой ошибке.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterOtherFailAfterTimeout() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new Exception()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" после успешного запроса выхода на линию.
   */
  @Test
  public void setHoldPendingViewStateToLiveDataForComplete() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние вида после таймера, после успешного запроса выхода на линию.
   */
  @Test
  public void setReadyViewStateToLiveDataForComplete() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHoldPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к списку ТС" если загрузка была успешной.
   */
  @Test
  public void setNavigateToVehiclesToLiveData() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.VEHICLE_OPTIONS);
  }

  /**
   * Должен вернуть "перейти к решению блокировки водителя" если водитель заблокирован.
   */
  @Test
  public void setNavigateToDriverBlockedToLiveData() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.DRIVER_BLOCKED);
  }

  /**
   * Должен вернуть "перейти к решению отсутствия свободных ТС" если нет свободных ТС.
   */
  @Test
  public void setNavigateToNoFreeVehiclesToLiveData() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoSuchElementException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.NO_FREE_VEHICLES);
  }

  /**
   * Должен вернуть "перейти к решению отсутствия любых ТС" если не никаких ТС.
   */
  @Test
  public void setNavigateToNoVehiclesToLiveData() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new EmptyListException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.NO_VEHICLES);
  }

  /**
   * Должен вернуть "перейти к ошибке сети".
   */
  @Test
  public void setNavigateToNoConnectionToLiveData() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть "перейти к ошибке формата данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorToLiveData() {
    // Given:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new Exception()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.goOnline();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}