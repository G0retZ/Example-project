package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateNotOnlineUseCaseTest {

  private ExecutorStateNotOnlineUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateSwitchGateway gateway;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() {
    when(gateway.sendNewExecutorState(any())).thenReturn(Completable.complete());
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(Flowable.never());
    useCase = new ExecutorStateNotOnlineUseCaseImpl(errorReporter, gateway, executorStateUseCase,
        ExecutorState.ONLINE, ExecutorState.PAYMENT_CONFIRMATION);
  }

  /* Проверяем работу с юзкейсом состояний */

  /**
   * Должен запросить получение смены состояний исполнителя.
   */
  @Test
  public void getExecutorStates() {
    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей передачи статусов, если статуса еще нет.
   */
  @Test
  public void DoNotTouchGatewayWithoutStatus() {
    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "смена закрыта".
   */
  @Test
  public void DoNotTouchGatewayIfShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "смена открыта".
   */
  @Test
  public void DoNotTouchGatewayIfShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "принятие заказа".
   */
  @Test
  public void DoNotTouchGatewayIfOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(
        Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "ожидание подтверждения клиента".
   */
  @Test
  public void DoNotTouchGatewayIfWaitForClientConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(
        Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "на пути к клиенту".
   */
  @Test
  public void DoNotTouchGatewayIfMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "ожидание клиента".
   */
  @Test
  public void DoNotTouchGatewayIfWaitingForClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей передачи статусов, если последний статус был "выполнение заказа".
   */
  @Test
  public void DoNotTouchGatewayIfOrderFulfillment() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен отправить статус "смена открыта" через гейтвей передачи статусов.
   */
  @Test
  public void askGatewayToSendNewExecutorStateIfOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ONLINE).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(gateway, only()).sendNewExecutorState(ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен отправить статус "смена открыта" через гейтвей передачи статусов.
   */
  @Test
  public void askGatewayToSendNewExecutorStateIfPaymentAcceptance() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(gateway, only()).sendNewExecutorState(ExecutorState.SHIFT_OPENED);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "смена закрыта".
   */
  @Test
  public void reportIllegalArgumentErrorIfShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "смена открыта".
   */
  @Test
  public void reportIllegalArgumentErrorIfOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "принятие заказа".
   */
  @Test
  public void reportIllegalArgumentErrorIfOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(
        Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "ожидания подтверждения клиентом".
   */
  @Test
  public void reportIllegalArgumentErrorIfWaitForClientConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(
        Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "на пути к клиенту".
   */
  @Test
  public void reportIllegalArgumentErrorIfMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "ожидание клиента".
   */
  @Test
  public void reportIllegalArgumentErrorIfWaitingForClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку неподходящего статуса, если статус "выполнение заказа".
   */
  @Test
  public void reportIllegalArgumentErrorIfOrderFulfillment() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Не должен отправлять ошибку при ошибке отправки статуса, если статус "онлайн".
   */
  @Test
  public void doNotReportError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ONLINE).concatWith(Flowable.never()));
    when(gateway.sendNewExecutorState(any()))
        .thenReturn(Completable.error(new Exception()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку при ошибке отправки статуса, если статус "прием оплаты".
   */
  @Test
  public void reportIllegalArgumentErrorIfPaymentAcceptance() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "смена закрыта".
   */
  @Test
  public void answerIllegalArgumentErrorIfShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "смена открыта".
   */
  @Test
  public void answerIllegalArgumentErrorIfOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "принятие заказа".
   */
  @Test
  public void answerIllegalArgumentErrorIfOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(
        Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "ожидания подтверждения клиентом".
   */
  @Test
  public void answerIllegalArgumentErrorIfWaitForClientConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(
        Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "на пути к клиенту".
   */
  @Test
  public void answerIllegalArgumentErrorIfMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "ожидание клиента".
   */
  @Test
  public void answerIllegalArgumentErrorIfWaitingForClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "выполнение заказа".
   */
  @Test
  public void answerIllegalArgumentErrorIfOrderFulfillment() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен вернуть ошибку при ошибке отправки статуса, если статус "онлайн".
   */
  @Test
  public void answerWithErrorIfOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ONLINE).concatWith(Flowable.never()));
    when(gateway.sendNewExecutorState(any()))
        .thenReturn(Completable.error(new Exception()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(Exception.class);
  }

  /**
   * Должен ответить завершением, если статус "онлайн".
   */
  @Test
  public void answerWithCompleteIfOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.ONLINE).concatWith(Flowable.never()));
    when(gateway.sendNewExecutorState(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен вернуть ошибку при ошибке отправки статуса, если статус "прием оплаты".
   */
  @Test
  public void answerWithErrorIfPaymentAcceptance() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION).concatWith(Flowable.never()));
    when(gateway.sendNewExecutorState(any()))
        .thenReturn(Completable.error(new Exception()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(Exception.class);
  }

  /**
   * Должен ответить завершением, если статус "прием оплаты".
   */
  @Test
  public void answerWithCompleteIfPaymentAcceptance() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(false))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION).concatWith(Flowable.never()));
    when(gateway.sendNewExecutorState(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }
}