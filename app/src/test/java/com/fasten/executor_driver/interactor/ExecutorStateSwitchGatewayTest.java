package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateSwitchGatewayTest {

  private ExecutorStateSwitchGateway gateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    gateway = new ExecutorStateSwitchGatewayImpl(stompClient);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendMessage() {
    // Действие:
    gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    verify(stompClient, only()).send("/mobile/status", "\"ONLINE\"");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfConnected() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerErrorIfConnected() {
    // Дано:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver =
        gateway.sendNewExecutorState(ExecutorState.ONLINE).test();

    // Результат:
    testObserver.assertError(IllegalArgumentException.class);
  }
}