package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateSwitchGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ExecutorStateSwitchGateway gateway;

  @Mock
  private ApiService apiService;

  @Before
  public void setUp() {
    gateway = new ExecutorStateSwitchGatewayImpl(apiService);
    when(apiService.switchStatus(any(ExecutorState.class))).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ переключить статус.
   */
  @Test
  public void askApiToSwitchStatus() {
    // Действие:
    gateway.sendNewExecutorState(ExecutorState.ONLINE).test().isDisposed();

    // Результат:
    verify(apiService, only()).switchStatus(ExecutorState.ONLINE);
  }

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfConnected() {
    // Дано:
    when(apiService.switchStatus(any(ExecutorState.class))).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerErrorIfConnected() {
    // Дано:
    when(apiService.switchStatus(any(ExecutorState.class)))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertError(IllegalArgumentException.class);
  }
}