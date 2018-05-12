package com.fasten.executor_driver.presentation.onlineswitch;

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
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.ForbiddenExecutorStateException;
import com.fasten.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
public class OnlineSwitchViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OnlineSwitchViewModel onlineSwitchViewModel;
  @Mock
  private ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase;
  @Mock
  private Observer<ViewState<OnlineSwitchViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private PublishSubject<ExecutorState> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(Completable.never());
    when(executorStateNotOnlineUseCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    onlineSwitchViewModel = new OnlineSwitchViewModelImpl(executorStateNotOnlineUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса источник для подписки изначально.
   */
  @Test
  public void askExecutorStateUseCaseForSubscribeInitially() {
    // Результат:
    verify(executorStateNotOnlineUseCase, only()).getExecutorStates();
  }

  /**
   * Должен перезапросить у юзкейса источник для подписки.
   */
  @Test
  public void askExecutorStateUseCaseForSubscribeOnceAgain() {
    // Действие:
    onlineSwitchViewModel.refreshStates();

    // Результат:
    verify(executorStateNotOnlineUseCase, times(2)).getExecutorStates();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase);
  }

  /**
   * Не должен трогать юзкейс при установке статуса "онлайн".
   */
  @Test
  public void doNotTouchUseCaseForSwitchToOnline() {
    // Действие:
    onlineSwitchViewModel.setNewState(true);
    onlineSwitchViewModel.setNewState(true);
    onlineSwitchViewModel.setNewState(true);

    // Результат:
    verify(executorStateNotOnlineUseCase, only()).getExecutorStates();
  }

  /**
   * Должен просить у юзкейса отправку статуса не онлайн, игнорируя последующие
   * запросы, пока не выполнился текущий.
   */
  @Test
  public void askUseCaseToSetNotOnlineOnlyOnce() {
    // Действие:
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    verify(executorStateNotOnlineUseCase).getExecutorStates();
    verify(executorStateNotOnlineUseCase).setExecutorNotOnline();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase);
  }

  /**
   * Должен просить у юзкейса отправку статусов не онлайн.
   */
  @Test
  public void askUseCaseToSetNotOnline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateNotOnlineUseCase);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.error(new ForbiddenExecutorStateException()),
        Completable.complete(),
        Completable.error(new ConnectionClosedException()),
        Completable.complete()
    );

    // Действие:
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(executorStateNotOnlineUseCase).getExecutorStates();
    inOrder.verify(executorStateNotOnlineUseCase, times(6)).setExecutorNotOnline();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase);
  }

  /**
   * Не должен трогать юзкейс при потреблении ошибки.
   */
  @Test
  public void doNotTouchExecutorStateUseCase() {
    // Действие:
    onlineSwitchViewModel.consumeServerError();
    onlineSwitchViewModel.consumeServerError();
    onlineSwitchViewModel.consumeServerError();

    // Результат:
    verify(executorStateNotOnlineUseCase, only()).getExecutorStates();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем по умолчанию.
   */
  @Test
  public void setUncheckedPendingViewStateInitially() {
    // Действие:
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "смена закрыта".
   */
  @Test
  public void setUncheckedPendingViewStateForShiftClosed() {
    // Дано:
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Результат:
    verify(viewStateObserver, times(2))
        .onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "принятие заказа".
   */
  @Test
  public void setUncheckedPendingViewStateForOrderConfirmation() {
    // Дано:
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Результат:
    verify(viewStateObserver, times(2))
        .onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "ожидание подтверждения клиента".
   */
  @Test
  public void setUncheckedPendingViewStateForWaitForClientConfirmation() {
    // Дано:
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Результат:
    verify(viewStateObserver, times(2))
        .onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "на пути к клиенту".
   */
  @Test
  public void setUncheckedPendingViewStateForMovingToClient() {
    // Дано:
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Результат:
    verify(viewStateObserver, times(2))
        .onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ошибки сокета с неактивным переключателем при ошибке в подписке на
   * статусы исполнителя.
   */
  @Test
  public void setUncheckedSocketErrorViewStateForExecutorStateError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new ConnectionClosedException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(any(OnlineSwitchViewStateUnCheckedSocketError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивным переключателем для "смена открыта".
   */
  @Test
  public void setUncheckedViewStateForShiftOpened() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedRegular.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с активным переключателем для "онлайн".
   */
  @Test
  public void setCheckedViewStateForOnline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateCheckedRegular.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивным переключателем для запроса установки онлайн.
   */
  @Test
  public void setUnCheckedViewStateForSetOnline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);
    onlineSwitchViewModel.setNewState(true);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(OnlineSwitchViewStateUnCheckedRegular.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для запроса установки не онлайн.
   */
  @Test
  public void setUnCheckedPendingViewStateForSetNotOnline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateCheckedRegular.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать дополнительный состояний для завершенного запроса установки не онлайн.
   */
  @Test
  public void setNoAdditionalViewStateForSetNotOnlineSuccess() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(Completable.complete());
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateCheckedRegular.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ошибки сервера с активным переключателем для запроса установки не онлайн.
   */
  @Test
  public void setUnCheckedServerErrorViewStateForSetNotOnline() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new ForbiddenExecutorStateException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateCheckedRegular.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateUnCheckedPending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OnlineSwitchViewStateCheckedServerError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть перейти к настройке параметров ТС.
   */
  @Test
  public void navigateToVehicleOptions() {
    // Действие:
    onlineSwitchViewModel.getNavigationLiveData().observeForever(navigateObserver);
    onlineSwitchViewModel.setNewState(true);

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineSwitchNavigate.VEHICLE_OPTIONS);
  }

  /**
   * Не должен никуда ходить.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.error(new NoNetworkException()),
        Completable.never()
    );

    // Действие:
    onlineSwitchViewModel.getNavigationLiveData().observeForever(navigateObserver);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}