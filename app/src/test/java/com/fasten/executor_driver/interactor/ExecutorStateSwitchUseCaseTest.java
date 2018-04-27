package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateSwitchUseCaseTest {

  private ExecutorStateSwitchUseCase executorStateSwitchUseCase;

  @Mock
  private ExecutorStateSwitchGateway executorStateSwitchGateway;

  @Before
  public void setUp() {
    when(executorStateSwitchGateway.sendNewExecutorState(any())).thenReturn(Completable.complete());
    executorStateSwitchUseCase = new ExecutorStateSwitchUseCaseImpl(executorStateSwitchGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен отправить полученную геопозицию через гейтвей передачи геолокаций.
   */
  @Test
  public void askGatewayToSendNewExecutorState() {
    // Действие:
    executorStateSwitchUseCase.setExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    verify(executorStateSwitchGateway, only()).sendNewExecutorState(ExecutorState.ONLINE);
  }

  /* Проверяем ответы гейтвея */

  /**
   * Должен вернуть ошибку при ошибке отправки статуса.
   */
  @Test
  public void answerWithError() {
    // Дано:
    when(executorStateSwitchGateway.sendNewExecutorState(any()))
        .thenReturn(Completable.error(new Exception()));

    // Действие:
    TestObserver<Void> testObserver =
        executorStateSwitchUseCase.setExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertError(Exception.class);
  }

  /**
   * Должен ответить завершением.
   */
  @Test
  public void answerWithComplete() {
    // Дано:
    when(executorStateSwitchGateway.sendNewExecutorState(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        executorStateSwitchUseCase.setExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }
}