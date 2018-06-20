package com.fasten.executor_driver.presentation.onlineswitch;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.ForbiddenExecutorStateException;
import com.fasten.executor_driver.gateway.DataMappingException;
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
        Completable.error(new IllegalStateException()),
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

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние ожидания.
   */
  @Test
  public void setUncheckedPendingViewStateInitially() {
    // Действие:
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(new OnlineSwitchViewStatePending(null));
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "смена закрыта".
   */
  @Test
  public void setUncheckedPendingViewStateForShiftClosed() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "принятие заказа".
   */
  @Test
  public void setCheckedPendingViewStateForOrderConfirmation() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "ожидание подтверждения клиента".
   */
  @Test
  public void setCheckedPendingViewStateForWaitForClientConfirmation() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "на пути к клиенту".
   */
  @Test
  public void setCheckedPendingViewStateForMovingToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "ожидание клиента".
   */
  @Test
  public void setCheckedPendingViewStateForWaitingForClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "выполнения заказа".
   */
  @Test
  public void setCheckedPendingViewStateForOrderFulfillment() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ошибки в данных с сервера с неактивным переключателем при ошибке в
   * подписке на статусы исполнителя.
   */
  @Test
  public void setServerDataErrorViewStateForExecutorStateError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStateServerDataError(null));
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
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(false));
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
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
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
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OnlineSwitchViewState(false));
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
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать дополнительных состояний для завершенного запроса установки не онлайн.
   */
  @Test
  public void setNoAdditionalViewStateForSetNotOnlineSuccess() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(Completable.complete());
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ошибки в данных от сервера с активным переключателем для запроса установки не онлайн.
   */
  @Test
  public void setUnCheckedServerErrorViewStateForSetNotOnline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new ForbiddenExecutorStateException()));
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStateServerDataError(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать состояние ошибки для запроса установки не онлайн, если была ошибка подключения.
   */
  @Test
  public void setNoAdditionalViewStateForSetNotOnlineIfNoConnection() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new IllegalStateException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    onlineSwitchViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
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
   * Должен вернуть перейти к ошибке сети.
   */
  @Test
  public void navigateToNoConnection() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new IllegalStateException()));

    // Действие:
    onlineSwitchViewModel.getNavigationLiveData().observeForever(navigateObserver);
    onlineSwitchViewModel.setNewState(false);

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineSwitchNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.error(new ForbiddenExecutorStateException()),
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