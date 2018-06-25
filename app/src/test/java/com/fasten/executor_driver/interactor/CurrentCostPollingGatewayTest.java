package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.CurrentCostPollingGatewayImpl;
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
public class CurrentCostPollingGatewayTest {

  private CurrentCostPollingGateway orderGateway;
  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    orderGateway = new CurrentCostPollingGatewayImpl(stompClient);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку пинга.
   */
  @Test
  public void askStompClientToSendReportPollPing() {
    // Действие:
    orderGateway.poll().test();

    // Результат:
    verify(stompClient, only()).send("/mobile/retrieveOverPackage", "\"\"");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты отправки сообщений серверу */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerPollPingSuccess() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.poll().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerPollPingError() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.error(new Exception()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.poll().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(Exception.class);
  }
}