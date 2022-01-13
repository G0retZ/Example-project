package com.cargopull.executor_driver.presentation.onlineswitch;

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
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class OnlineSwitchViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OnlineSwitchViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
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
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(Completable.never());
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OnlineSwitchViewModelImpl(errorReporter, executorStateNotOnlineUseCase,
        executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку получения данных.
   */
  @Test
  public void reportIllegalArgumentErrorIfBlocked() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса.
   */
  @Test
  public void reportIllegalArgumentErrorIfShiftClosed() {
    // Given:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(IllegalArgumentException::new));

    // Action:
    viewModel.setNewState(false);

    // Effect:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен просить у юзкейса источник для подписки изначально.
   */
  @Test
  public void askExecutorStateUseCaseForSubscribeInitially() {
    // Effect:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс при установке статуса "онлайн".
   */
  @Test
  public void doNotTouchUseCaseForSwitchToOnline() {
    // Action:
    viewModel.setNewState(true);
    viewModel.setNewState(true);
    viewModel.setNewState(true);

    // Effect:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Должен просить у юзкейса отправку статуса не онлайн, игнорируя последующие запросы, пока не
   * выполнился текущий.
   */
  @Test
  public void askUseCaseToSetNotOnlineOnlyOnce() {
    // Action:
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);

    // Effect:
    verify(executorStateUseCase).getExecutorStates();
    verify(executorStateNotOnlineUseCase).setExecutorNotOnline();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase, executorStateUseCase);
  }

  /**
   * Должен просить у юзкейса отправку статусов не онлайн.
   */
  @Test
  public void askUseCaseToSetNotOnline() {
    // Given:
    InOrder inOrder = Mockito.inOrder(executorStateNotOnlineUseCase, executorStateUseCase);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.error(new Exception()),
        Completable.complete(),
        Completable.error(new IllegalStateException()),
        Completable.complete()
    );

    // Action:
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);
    viewModel.setNewState(false);

    // Effect:
    inOrder.verify(executorStateUseCase).getExecutorStates();
    inOrder.verify(executorStateNotOnlineUseCase, times(6)).setExecutorNotOnline();
    verifyNoMoreInteractions(executorStateNotOnlineUseCase, executorStateUseCase);
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние ожидания.
   */
  @Test
  public void setUncheckedPendingViewStateInitially() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(viewStateObserver, only()).onChanged(new OnlineSwitchViewStatePending(null));
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "заблокирован".
   */
  @Test
  public void setUncheckedPendingViewStateForBlocked() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(false));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для "смена закрыта".
   */
  @Test
  public void setUncheckedPendingViewStateForShiftClosed() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(false));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "принятие заказа".
   */
  @Test
  public void setCheckedPendingViewStateForOrderConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "принятие заказа".
   */
  @Test
  public void setCheckedPendingViewStateForPreOrderConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "ожидание подтверждения
   * клиента".
   */
  @Test
  public void setCheckedPendingViewStateForWaitForClientConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "на пути к клиенту".
   */
  @Test
  public void setCheckedPendingViewStateForMovingToClient() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "ожидание клиента".
   */
  @Test
  public void setCheckedPendingViewStateForWaitingForClient() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "выполнения заказа".
   */
  @Test
  public void setCheckedPendingViewStateForOrderFulfillment() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с активным переключателем для "приема оплаты".
   */
  @Test
  public void setCheckedPendingViewStateForPaymentAcceptance() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.PAYMENT_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка в подписке на статусы исполнителя.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForExecutorStateError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(new OnlineSwitchViewStatePending(null));
  }

  /**
   * Должен вернуть состояние с неактивным переключателем для "смена открыта".
   */
  @Test
  public void setUncheckedViewStateForShiftOpened() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(false));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с активным переключателем для "онлайн".
   */
  @Test
  public void setCheckedViewStateForOnline() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивным переключателем для запроса установки онлайн.
   */
  @Test
  public void setUnCheckedViewStateForSetOnline() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);
    viewModel.setNewState(true);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver, times(2)).onChanged(new OnlineSwitchViewState(false));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние ожидания с неактивным переключателем для запроса установки не онлайн.
   */
  @Test
  public void setUnCheckedPendingViewStateForSetNotOnline() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать состояние ошибки для запроса установки не онлайн, если была ошибка
   * подключения.
   */
  @Test
  public void setCheckedViewStateForSetNotOnlineIfNoConnection() {
    // Given:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new IllegalStateException()));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new Exception()));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.setNewState(false);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    inOrder.verify(viewStateObserver)
        .onChanged(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
    inOrder.verify(viewStateObserver).onChanged(new OnlineSwitchViewState(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorForExecutorStateError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorForSetNotOnlineError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new Exception()));

    // Action:
    viewModel.setNewState(false);

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть перейти к ошибке сети.
   */
  @Test
  public void navigateToNoConnectionForSetNotOnlineError() {
    // Given:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline())
        .thenReturn(Completable.error(new IllegalStateException()));

    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.setNewState(false);

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Given:
    when(executorStateNotOnlineUseCase.setExecutorNotOnline()).thenReturn(
        Completable.complete(),
        Completable.never()
    );

    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.setNewState(false);
    viewModel.setNewState(false);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к настройке параметров ТС.
   */
  @Test
  public void navigateToVehicleOptions() {
    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.setNewState(true);

    // Effect:
    verify(navigateObserver, only()).onChanged(OnlineSwitchNavigate.VEHICLE_OPTIONS);
  }
}