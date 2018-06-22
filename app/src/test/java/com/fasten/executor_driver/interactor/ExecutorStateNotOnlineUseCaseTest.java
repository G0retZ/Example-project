package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.ForbiddenExecutorStateException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateNotOnlineUseCaseTest {

  private ExecutorStateNotOnlineUseCase useCase;

  @Mock
  private ExecutorStateSwitchGateway gateway;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() {
    when(gateway.sendNewExecutorState(any())).thenReturn(Completable.complete());
    when(executorStateUseCase.getExecutorStates(false)).thenReturn(Flowable.never());
    useCase = new ExecutorStateNotOnlineUseCaseImpl(gateway,
        executorStateUseCase);
  }

  /* Проверяем работу с юзкейсом состояний */

  /**
   * Должен запросить получение смены состояний исполнителя.
   */
  @Test
  public void getExecutorStates() {
    // Действие:
    useCase.getExecutorStates().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей передачи статусов, если текущего статуса нет.
   */
  @Test
  public void DoNotTouchGatewayWithoutStatus() {
    // Дано:
    useCase.getExecutorStates().test();

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

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
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен отправить статус "смена открыта" через гейтвей передачи статусов.
   */
  @Test
  public void askGatewayToSendNewExecutorState() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.ONLINE);

    // Действие:
    useCase.setExecutorNotOnline().test();

    // Результат:
    verify(gateway, only()).sendNewExecutorState(ExecutorState.SHIFT_OPENED);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть ошибку неподходящего статуса, если статуса нет.
   */
  @Test
  public void answerForbiddenStatusErrorIfNoStatus() {
    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "смена закрыта".
   */
  @Test
  public void answerForbiddenStatusErrorIfShiftClosed() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "смена открыта".
   */
  @Test
  public void answerForbiddenStatusErrorIfOnline() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "принятие заказа".
   */
  @Test
  public void answerForbiddenStatusErrorIfOrderConfirmation() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "ожидания подтверждения клиентом".
   */
  @Test
  public void answerForbiddenStatusErrorIfWaitForClientConfirmation() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "на пути к клиенту".
   */
  @Test
  public void answerForbiddenStatusErrorIfMovingToClient() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "ожидание клиента".
   */
  @Test
  public void answerForbiddenStatusErrorIfWaitingForClient() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку неподходящего статуса, если статус "выполнение заказа".
   */
  @Test
  public void answerForbiddenStatusErrorIfOrderFulfillment() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ForbiddenExecutorStateException.class);
  }

  /**
   * Должен вернуть ошибку при ошибке отправки статуса.
   */
  @Test
  public void answerWithError() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    when(gateway.sendNewExecutorState(any()))
        .thenReturn(Completable.error(new Exception()));
    publishSubject.onNext(ExecutorState.ONLINE);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(Exception.class);
  }

  /**
   * Должен ответить завершением.
   */
  @Test
  public void answerWithComplete() {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    useCase.getExecutorStates().test();
    when(gateway.sendNewExecutorState(any())).thenReturn(Completable.complete());
    publishSubject.onNext(ExecutorState.ONLINE);

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }
}