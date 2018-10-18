package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBalanceUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ExecutorBalanceUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CommonGateway<ExecutorBalance> gateway;
  @Mock
  private ExecutorBalance executorBalance;
  @Mock
  private ExecutorBalance executorBalance1;
  @Mock
  private ExecutorBalance executorBalance2;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new ExecutorBalanceUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея баланс исполнителя только раз.
   */
  @Test
  public void askGatewayForExecutorBalance() {
    // Действие:
    useCase.getExecutorBalance().test();
    useCase.getExecutorBalance().test();
    useCase.getExecutorBalance().test();
    useCase.getExecutorBalance().test();

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getExecutorBalance().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть баланс исполнителя.
   */
  @Test
  public void answerWithExecutorBalance() {
    // Дано:
    when(gateway.getData())
        .thenReturn(Flowable.just(executorBalance, executorBalance2, executorBalance1));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber = useCase.getExecutorBalance().test();

    // Результат:
    testSubscriber.assertValues(executorBalance, executorBalance2, executorBalance1);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber = useCase.getExecutorBalance().test();

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
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber = useCase.getExecutorBalance().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}