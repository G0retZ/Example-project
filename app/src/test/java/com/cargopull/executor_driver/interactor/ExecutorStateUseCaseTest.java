package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ExecutorStateUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateGateway gateway;

  @Before
  public void setUp() {
    when(gateway.getState()).thenReturn(Flowable.never());
    useCase = new ExecutorStateUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея баланс исполнителя только раз.
   */
  @Test
  public void askGatewayForExecutorState() {
    // Действие:
    useCase.getExecutorStates().test();
    useCase.getExecutorStates().test();
    useCase.getExecutorStates().test();
    useCase.getExecutorStates().test();

    // Результат:
    verify(gateway, only()).getState();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Дано:
    when(gateway.getState()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getExecutorStates().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть баланс исполнителя.
   */
  @Test
  public void answerWithExecutorState() {
    // Дано:
    when(gateway.getState()).thenReturn(
        Flowable.just(ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
            ExecutorState.DRIVER_ORDER_CONFIRMATION, ExecutorState.CLIENT_ORDER_CONFIRMATION,
            ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
            ExecutorState.ORDER_FULFILLMENT, ExecutorState.PAYMENT_CONFIRMATION)
    );

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = useCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertValues(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, ExecutorState.CLIENT_ORDER_CONFIRMATION,
        ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
        ExecutorState.ORDER_FULFILLMENT, ExecutorState.PAYMENT_CONFIRMATION
    );
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Дано:
    when(gateway.getState()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = useCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение баланса исполнителя.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(gateway.getState()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber = useCase.getExecutorStates().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}