package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;

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
    // Action:
    gateway.sendNewExecutorState(ExecutorState.ONLINE).test().isDisposed();

    // Effect:
    verify(apiService, only()).switchStatus(ExecutorState.ONLINE);
  }

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfConnected() {
    // Given:
    when(apiService.switchStatus(any(ExecutorState.class))).thenReturn(Completable.complete());

    // Action:
    TestObserver<Void> testObserver = gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Effect:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerErrorIfConnected() {
    // Given:
    when(apiService.switchStatus(any(ExecutorState.class)))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Action:
    TestObserver<Void> testObserver = gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Effect:
    testObserver.assertError(IllegalArgumentException.class);
  }
}