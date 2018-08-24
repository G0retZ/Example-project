package com.cargopull.executor_driver.presentation.executorstate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
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
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ExecutorStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ExecutorStateViewActions>> viewStateCaptor;
  @Mock
  private ExecutorStateViewActions viewActions;

  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.never());
    viewModel = new ExecutorStateViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы исполнителя со сбросом кеша.
   */
  @Test
  public void askUseCaseToSubscribeToExecutorStateUpdatesWithCacheReset() {
    // Действие:
    viewModel.initializeExecutorState();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Должен просить у юзкейса загрузить статусы исполнителя, без сброса кеша, даже если уже
   * подписан.
   */
  @Test
  public void doNotTouchUseCaseBeforeFirstRequestComplete() {
    // Действие:
    viewModel.initializeExecutorState();
    viewModel.initializeExecutorState();
    viewModel.initializeExecutorState();

    // Результат:
    verify(executorStateUseCase, times(3)).getExecutorStates();
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /**
   * Не должен трогать юзкейса.
   */
  @Test
  public void doNotTouchUseCaseForMessageReadEvent() {
    // Действие:
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Результат:
    verifyZeroInteractions(executorStateUseCase);
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сопутствующее открытой смене сообщение.
   */
  @Test
  public void showShiftOpenedMessage() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData("Message");
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showOnlineMessage("Message");
  }

  /**
   * Должен показать сопутствующее открытой смене сообщение, затем null после его прочтения.
   */
  @Test
  public void showShiftOpenedMessageThenNull() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData("Message");
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();
    viewModel.messageConsumed();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showOnlineMessage("Message");
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать null сообщение.
   */
  @Test
  public void doNotShowNullShiftOpenedMessage() {
    // Дано:
    ExecutorState.SHIFT_OPENED.setData(null);
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

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
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

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
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showOnlineMessage("Message");
  }

  /**
   * Должен показать сопутствующее онлайн сообщение, затем null после его прочтения.
   */
  @Test
  public void showOnlineMessageThenNull() {
    // Дано:
    ExecutorState.ONLINE.setData("Message");
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();
    viewModel.messageConsumed();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    assertEquals(2, viewStateCaptor.getAllValues().size());
    assertNull(viewStateCaptor.getAllValues().get(1));
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    verify(viewActions, only()).showOnlineMessage("Message");
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать null сообщение.
   */
  @Test
  public void doNotShowNullOnlineMessage() {
    // Дано:
    ExecutorState.ONLINE.setData(null);
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

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
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

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
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorState();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Должен вернуть "перейти к отсутствию сети".
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к отсутствию сети".
   */
  @Test
  public void navigateToAuthorize() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к карте".
   */
  @Test
  public void navigateToShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_CLOSED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_OPENED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_ONLINE);
  }

  /**
   * Должен вернуть "перейти к подтверждению заказа".
   */
  @Test
  public void navigateToDriverOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к ожиданию подтверждения клиента".
   */
  @Test
  public void navigateToClientOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к движению к клиенту".
   */
  @Test
  public void navigateToMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MOVING_TO_CLIENT);
  }

  /**
   * Должен вернуть "перейти к ожиданию клиента".
   */
  @Test
  public void navigateToWaitingForClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.WAITING_FOR_CLIENT);
  }

  /**
   * Должен вернуть "перейти к выполнению заказа".
   */
  @Test
  public void navigateToOrderFulfillment() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.ORDER_FULFILLMENT);
  }

  /**
   * Должен вернуть "перейти к приему оплаты".
   */
  @Test
  public void navigateToPaymentAcceptance() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorState();

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.PAYMENT_CONFIRMATION);
  }
}