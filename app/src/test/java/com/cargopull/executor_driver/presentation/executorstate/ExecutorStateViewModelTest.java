package com.cargopull.executor_driver.presentation.executorstate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ExecutorStateViewModel viewModel;
  @Mock
  private ExecutorStateUseCase useCase;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ExecutorStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ExecutorStateViewActions>> viewStateCaptor;
  @Mock
  private ExecutorStateViewActions viewActions;

  private PublishSubject<ExecutorState> publishSubject;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    ExecutorState.SHIFT_OPENED.setData(null);
    publishSubject = PublishSubject.create();
    when(useCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new ExecutorStateViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы исполнителя только при создании.
   */
  @Test
  public void askUseCaseToSubscribeToExecutorStateUpdatesInitially() {
    // Результат:
    verify(useCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на прочтении сообщений.
   */
  @Test
  public void doNotTouchUseCaseForMessageReadEvent() {
    // Действие:
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Результат:
    verify(useCase, only()).getExecutorStates();
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сопутствующее открытой смене сообщение.
   */
  @Test
  public void showBlockedMessage() {
    // Дано:
    ExecutorState.BLOCKED.setData("Message");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showExecutorStatusInfo("Message");
  }

  /**
   * Должен показать сопутствующее открытой смене сообщение, затем null после его прочтения.
   */
  @Test
  public void showBlockedMessageThenNull() {
    // Дано:
    ExecutorState.BLOCKED.setData("Message");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.BLOCKED);
    viewModel.messageConsumed();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showExecutorStatusInfo("Message");
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать null сообщение.
   */
  @Test
  public void doNotShowNullBlockedMessage() {
    // Дано:
    ExecutorState.BLOCKED.setData(null);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyBlockedMessage() {
    // Дано:
    ExecutorState.BLOCKED.setData("");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Должен показать сопутствующее открытой смене сообщение.
   */
  @Test
  public void showShiftOpenedMessage() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData("Message");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showExecutorStatusMessage("Message");
  }

  /**
   * Должен показать сопутствующее открытой смене сообщение, затем null после его прочтения.
   */
  @Test
  public void showShiftOpenedMessageThenNull() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData("Message");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);
    viewModel.messageConsumed();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showExecutorStatusMessage("Message");
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать null сообщение.
   */
  @Test
  public void doNotShowNullShiftOpenedMessage() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData(null);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyShiftOpenedMessage() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData("");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Должен показать сопутствующее онлайн сообщение.
   */
  @Test
  public void showOnlineMessage() {
    // Дано:
    ExecutorState.ONLINE.setData("Message");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showExecutorStatusMessage("Message");
  }

  /**
   * Должен показать сопутствующее онлайн сообщение, затем null после его прочтения.
   */
  @Test
  public void showOnlineMessageThenNull() {
    // Дано:
    ExecutorState.ONLINE.setData("Message");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    viewModel.messageConsumed();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showExecutorStatusMessage("Message");
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать null сообщение.
   */
  @Test
  public void doNotShowNullOnlineMessage() {
    // Дано:
    ExecutorState.ONLINE.setData(null);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyOnlineMessage() {
    // Дано:
    ExecutorState.ONLINE.setData("");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать сообщение из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Дано:
    ExecutorState.ONLINE.setData("\n");
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void navigateToNowhere() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера".
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к экрану блокировки".
   */
  @Test
  public void navigateToBlocked() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.BLOCKED);
  }

  /**
   * Должен вернуть "перейти к карте".
   */
  @Test
  public void navigateToShiftClosed() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_CLOSED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToShiftOpened() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_OPENED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToOnline() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_ONLINE);
  }

  /**
   * Должен вернуть "перейти к подтверждению заказа".
   */
  @Test
  public void navigateToDriverOrderConfirmation() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к подтверждению предварительного заказа".
   */
  @Test
  public void navigateToDriverPreOrderConfirmation() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION);

    // Результат:
    verify(navigationObserver, only())
        .onChanged(ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к ожиданию подтверждения клиента".
   */
  @Test
  public void navigateToClientOrderConfirmation() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к движению к клиенту".
   */
  @Test
  public void navigateToMovingToClient() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MOVING_TO_CLIENT);
  }

  /**
   * Должен вернуть "перейти к ожиданию клиента".
   */
  @Test
  public void navigateToWaitingForClient() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.WAITING_FOR_CLIENT);
  }

  /**
   * Должен вернуть "перейти к выполнению заказа".
   */
  @Test
  public void navigateToOrderFulfillment() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.ORDER_FULFILLMENT);
  }

  /**
   * Должен вернуть "перейти к приему оплаты".
   */
  @Test
  public void navigateToPaymentAcceptance() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.PAYMENT_CONFIRMATION);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.PAYMENT_CONFIRMATION);
  }
}