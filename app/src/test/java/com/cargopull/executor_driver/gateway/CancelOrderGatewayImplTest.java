package com.cargopull.executor_driver.gateway;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.interactor.CancelOrderGateway;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CancelOrderGateway gateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private CancelOrderReason cancelOrderReason;

  @Before
  public void setUp() {
    gateway = new CancelOrderGatewayImpl(stompClient);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку причины отказа.
   */
  @Test
  public void askStompClientToSendCancelOrderReason() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");

    // Действие:
    gateway.cancelOrder(cancelOrderReason).test().isDisposed();

    // Результат:
    verify(stompClient, only())
        .send("/mobile/takeOffOrder", "{\"id\":7,\"description\":\"seven\"}");
  }

  /* Проверяем результаты обработки сообщений от сервера по причинам для отказа */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCancelOrderReasonSuccess() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.cancelOrder(cancelOrderReason).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerCancelOrderReasonError() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.cancelOrder(cancelOrderReason).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}