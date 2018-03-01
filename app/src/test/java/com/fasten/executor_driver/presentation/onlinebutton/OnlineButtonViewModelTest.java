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

  private OnlineButtonViewModel onlineButtonViewModel;

  private TestScheduler testScheduler;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private Observer<ViewState<OnlineButtonViewActions>> viewStateObserver;

  @Mock
  private VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() throws Exception {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(vehiclesAndOptionsUseCase.loadVehicles()).thenReturn(Completable.never());
    onlineButtonViewModel = new OnlineButtonViewModelImpl(vehiclesAndOptionsUseCase);
  }

  /* Тетсируем работу с юзкейсом выхода на линию. */

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchOnlineUseCaseUntilRequestFinished() throws Exception {
    // Действие:
    onlineButtonViewModel.goOnline();
    onlineButtonViewModel.goOnline();
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(vehiclesAndOptionsUseCase, only()).loadVehicles();
  }

  /**
   * Не должен просить юзкейс выйти на линию, если предыдущий запрос еще не завершился.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchOnlineUseCaseToGoOnlineUntilTimeout() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(vehiclesAndOptionsUseCase);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(vehiclesAndOptionsUseCase).loadVehicles();
    inOrder.verifyNoMoreInteractions();
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    inOrder.verify(vehiclesAndOptionsUseCase).loadVehicles();
    verifyNoMoreInteractions(vehiclesAndOptionsUseCase);

  }

  /**
   * Должен попросить юзкейс отправить выйти на линию.
   *
   * @throws Exception error
   */
  @Test
  public void askOnlineUseCaseToGoOnline() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    verify(vehiclesAndOptionsUseCase, times(3)).loadVehicles();
    verifyNoMoreInteractions(vehiclesAndOptionsUseCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть рабочее состояние вида.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveData() throws Exception {
    // Действие:
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(OnlineButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" для запроса выхода на линию.
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если водитель заблокирован.
   *
   * @throws Exception error
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForBlockedDriver() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new DriverBlockedException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если водитель заблокирован.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForBlockedDriver() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new DriverBlockedException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если недостаточно средств.
   *
   * @throws Exception error
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForInsufficientCredits() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new InsufficientCreditsException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если недостаточно средств.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForInsufficientCredits() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new InsufficientCreditsException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если нет ТС.
   *
   * @throws Exception error
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForNoVehicles() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoVehiclesAvailableException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если нет ТС.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoVehicles() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoVehiclesAvailableException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен переходить в состояние вида "Ошибка", если нет свободных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void doNotSetErrorViewStateToLiveDataAfterFailForNoFreeVehicles() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoFreeVehiclesException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть рабочее состояние после таймера, если нет свободных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailForNoFreeVehicles() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoFreeVehiclesException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateHold.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" после ошибки запроса выхода на линию.
   *
   * @throws Exception error
   */
  @Test
  public void setErrorViewStateToLiveDataAfterFail() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

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
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveDataAfterFailConsumedBeforeTimeout() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    onlineButtonViewModel.consumeError();

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
   *
   * @throws Exception error
   */
  @Test
  public void doNotUnsetErrorViewStateToLiveDataAfterFailNotConsumedAfterTimeout()
      throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
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
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveDataAfterFailConsumedAfterTimeout() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    onlineButtonViewModel.consumeError();
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
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveDataAfterFailAfterTimeout() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    onlineButtonViewModel.consumeError();

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
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();
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
   *
   * @throws Exception error
   */
  @Test
  public void setNothingToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoNetworkException()));
    onlineButtonViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к списку ТС" если загрузка была успешной.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToVehiclesToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles()).thenReturn(Completable.complete());
    onlineButtonViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.VEHICLE_OPTIONS);
  }

  /**
   * Должен вернуть "перейти к к решению блокировки водителя" если водитель заблокирован.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToDriverBlockedToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new DriverBlockedException()));
    onlineButtonViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.DRIVER_BLOCKED);
  }

  /**
   * Должен вернуть "перейти к решению недостатка средств" если недостаточно средств.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToInsufficientCreditsToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new InsufficientCreditsException()));
    onlineButtonViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.INSUFFICIENT_CREDITS);
  }

  /**
   * Должен вернуть "перейти к решению отсутствия свободных ТС" если нет свободных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToNoFreeVehiclesToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoFreeVehiclesException()));
    onlineButtonViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.NO_FREE_VEHICLES);
  }

  /**
   * Должен вернуть "перейти к решению отсутствия любых ТС" если не никаких ТС.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToNoVehiclesToLiveData() throws Exception {
    // Дано:
    when(vehiclesAndOptionsUseCase.loadVehicles())
        .thenReturn(Completable.error(new NoVehiclesAvailableException()));
    onlineButtonViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    onlineButtonViewModel.goOnline();

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineButtonNavigate.NO_VEHICLES);
  }
}