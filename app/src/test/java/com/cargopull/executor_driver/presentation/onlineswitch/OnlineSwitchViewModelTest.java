package com.cargopull.executor_driver.presentation.onlineswitch;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
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
  private OnlineSwitchViewModel viewModel;
  @Mock
  private ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;
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
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OnlineSwitchViewModelImpl(executorStateNotOnlineUseCase, executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса источник для подписки изначально.
   */
  @Test
  public void askExecutorStateUseCaseForSubscribeInitially() {
    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /**
   * Не должен трогать юзкейс при установке статуса "онлайн".
   */
  @Test
  public void doNotTouchUseCaseForSwitchToOnline() {
    // Действие:
    viewModel.setNewState(true);
    viewModel.setNewState(true);
    viewModel.setNewState(true);

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /**
   * Должен просить у юзкейса отправку статуса не онлайн, игнорируя последующие
   * запросы, пока не выполнился текущий.
   */
  @Test
  public void askUseCaseToSetNotOnlineOnlyOnce() {
    // Действие:
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);

    // Результат:
    verify(executorStateUseCase).getExecutorStates(false);
    verify(executorStateNotOnlineUseCase).setExecutorNotOnline();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase, executorStateUseCase);
  }

  /**
   * Должен просить у юзкейса отправку статусов не онлайн.
   */
  @Test
  public void askUseCaseToSetNotOnline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateNotOnlineUseCase, executorStateUseCase);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.error(new Exception()),
        Completable.complete(),
        Completable.error(new IllegalStateException()),
        Completable.complete()
    );

    // Действие:
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);

    // Результат:
    inOrder.verify(executorStateUseCase).getExecutorStates(false);
    inOrder.verify(executorStateNotOnlineUseCase, times(6)).setExecutorNotOnline();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase, executorStateUseCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние ожидания.
   */
  @Test
  public void setUncheckedPendingViewStateInitially() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "приема оплаты".
   */
  @Test
  public void setCheckedPendingViewStateForPaymentAcceptance() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.PAYMENT_CONFIRMATION);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка в подписке на статусы исполнителя.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForExecutorStateError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new OnlineSwitchViewStatePending(null));
  }

  /**
   * Должен вернуть состояние с неактивным переключателем для "смена открыта".
   */
  @Test
  public void setUncheckedViewStateForShiftOpened() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);
    viewModel.setNewState(true);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать состояние ошибки для запроса установки не онлайн, если была ошибка подключения.
   */
  @Test
  public void setCheckedViewStateForSetNotOnlineIfNoConnection() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new IllegalStateException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка в запросе установки не онлайн.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForSetNotOnlineError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new Exception()));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorForExecutorStateError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorForSetNotOnlineError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new Exception()));

    // Действие:
    viewModel.setNewState(false);

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть перейти к ошибке сети.
   */
  @Test
  public void navigateToNoConnectionForSetNotOnlineError() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new IllegalStateException()));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.setNewState(false);

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Дано:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.setNewState(false);
    viewModel.setNewState(false);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к настройке параметров ТС.
   */
  @Test
  public void navigateToVehicleOptions() {
    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.setNewState(true);

    // Результат:
    verify(navigateObserver, only()).onChanged(OnlineSwitchNavigate.VEHICLE_OPTIONS);
  }
}