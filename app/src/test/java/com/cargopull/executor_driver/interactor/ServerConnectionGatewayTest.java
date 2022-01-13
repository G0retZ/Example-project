package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.gateway.ServerConnectionGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class ServerConnectionGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ServerConnectionGateway gateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    gateway = new ServerConnectionGatewayImpl(stompClient);
    when(stompClient.getConnectionState()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP подписку на событие соединения.
   */
  @Test
  public void askStompClientToSubscribeForConnectionState() {
    // Action:
    gateway.getSocketState().test().isDisposed();

    // Effect:
    verify(stompClient, only()).getConnectionState();
  }

  /* Проверяем ответы на попытку открытия сокета сообщения */

  /**
   * Не должен ничем отвечать.
   */
  @Test
  public void answerNothing() {
    // Action:
    TestSubscriber<Boolean> testSubscriber = gateway.getSocketState().test();

    // Effect:
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerOpenSuccess() {
    // Given:
    when(stompClient.getConnectionState()).thenReturn(
        Flowable.<Boolean>never().startWith(true).startWith(false)
    );

    // Action:
    TestSubscriber<Boolean> testSubscriber = gateway.getSocketState().test();

    // Effect:
    testSubscriber.assertNotComplete();
    testSubscriber.assertValues(false, true);
  }

  /**
   * Должен ответить ошибкой, если соединение провалилось.
   */
  @Test
  public void answerWithFalseAndErrorAfterFailed() {
    // Given:
    when(stompClient.getConnectionState()).thenReturn(
        Flowable.<Boolean>error(new NoNetworkException()).startWith(false)
    );

    // Action:
    TestSubscriber<Boolean> testSubscriber = gateway.getSocketState().test();

    // Effect:
    testSubscriber.assertError(NoNetworkException.class);
    testSubscriber.assertValue(false);
    testSubscriber.assertNotComplete();
  }
}