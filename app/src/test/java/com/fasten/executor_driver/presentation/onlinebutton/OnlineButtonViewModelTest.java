package com.fasten.executor_driver.presentation.onlinebutton;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.DriverBlockedException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OnlineButtonViewModel viewModel;
  private TestScheduler testScheduler;
  @Mock
  private Observer<ViewState<OnlineButtonViewActions>> viewStateObserver;

  @Mock
  private VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.never());
    viewModel = new OnlineButtonViewModelImpl(vehiclesAndOptionsUseCase);
  }

  /* Тетсируем работу с юзкейсом выхода на линию. */

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   */
  @Test
  public void DoNotTouchOnlineUseCaseUntilRequestFinished() {
    // Действие:
    viewModel.goOnline();
    viewModel.goOnline();
    viewModel.goOnline();

    // Результат:
    verify(vehiclesAndOptionsUseCase, only()).loadVehiclesAndOptions();
  }

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   */
  @Test
  public void DoNotTouchOnlineUseCaseToGoOnlineUntilTimeout() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(vehiclesAndOptionsUseCase);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    viewModel.goOnline();
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    viewModel.goOnline();

    // Результат:
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
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    verify(vehiclesAndOptionsUseCase, times(3)).loadVehiclesAndOptions();
    verifyNoMoreInteractions(vehiclesAndOptionsUseCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть рабочее состояние вида.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(OnlineButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" для запроса выхода на линию.
   */
  @Test
  public void setPendingViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если водитель заблокирован.
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForBlockedDriver() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если водитель заблокирован.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForBlockedDriver() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если недостаточно средств.
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForInsufficientCredits() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new InsufficientCreditsException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если недостаточно средств.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForInsufficientCredits() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new InsufficientCreditsException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если нет ТС.
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForNoVehicles() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoVehiclesAvailableException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если нет ТС.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoVehicles() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoVehiclesAvailableException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если нет свободных ТС.
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForNoFreeVehicles() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoFreeVehiclesException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если нет свободных ТС.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoFreeVehicles() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoFreeVehiclesException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" после ошибки запроса выхода на линию.
   */
  @Test
  public void setErrorViewStateToLiveDataAfterFail() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineButtonViewStateError(new NoNetworkException()));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ожидайте" после ошибки запроса выхода на линию, если ошибка была
   * потреблена до истечения таймера.
   */
  @Test
  public void setHoldViewStateToLiveDataAfterFailConsumedBeforeTimeout() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    viewModel.consumeError();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineButtonViewStateError(new NoNetworkException()));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен оставить состояние вида "Ошибка", если она не была потреблена после истечения таймера.
   */
  @Test
  public void doNotUnsetErrorViewStateToLiveDataAfterFailNotConsumedAfterTimeout() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineButtonViewStateError(new NoNetworkException()));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ожидайте" с возвратом обратно в состояние готовности после
   * ошибки запроса выхода на линию и ее потребления.
   */
  @Test
  public void setHoldViewStateToLiveDataAfterFailConsumedAfterTimeout() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    viewModel.consumeError();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineButtonViewStateError(new NoNetworkException()));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние рабочее вида после ошибки запроса выхода на линию, если она была
   * потреблена после истечения таймера.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailAfterTimeout() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    viewModel.consumeError();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineButtonViewStateError(new NoNetworkException()));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" с возвратом обратно в состояние готовности после
   * успешного запроса выхода на линию.
   */
  @Test
  public void setHoldViewStateToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать прочие ошибки.
   */
  @Test
  public void setNothingToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoNetworkException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к списку ТС" если загрузка была успешной.
   */
  @Test
  public void setNavigateToVehiclesToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.VEHICLE_OPTIONS);
  }

  /**
   * Должен вернуть "перейти к решению блокировки водителя" если водитель заблокирован.
   */
  @Test
  public void setNavigateToDriverBlockedToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new DriverBlockedException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.DRIVER_BLOCKED);
  }

  /**
   * Должен вернуть "перейти к решению недостатка средств" если недостаточно средств.
   */
  @Test
  public void setNavigateToInsufficientCreditsToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new InsufficientCreditsException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.INSUFFICIENT_CREDITS);
  }

  /**
   * Должен вернуть "перейти к решению отсутствия свободных ТС" если нет свободных ТС.
   */
  @Test
  public void setNavigateToNoFreeVehiclesToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoFreeVehiclesException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.NO_FREE_VEHICLES);
  }

  /**
   * Должен вернуть "перейти к решению отсутствия любых ТС" если не никаких ТС.
   */
  @Test
  public void setNavigateToNoVehiclesToLiveData() {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehiclesAndOptions())
        .thenReturn(Completable.error(new NoVehiclesAvailableException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.NO_VEHICLES);
  }
}