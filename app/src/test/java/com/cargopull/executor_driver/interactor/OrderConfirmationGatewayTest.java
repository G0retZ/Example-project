package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.OrderConfirmationGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderConfirmationGateway gateway;
  @Mock
  private StompClient stompClient;
  @Mock
  private Order order;

  @Before
  public void setUp() {
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    gateway = new OrderConfirmationGatewayImpl(stompClient);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendMessage() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(order.getId()).thenReturn(7L);

    // Действие:
    gateway.sendDecision(order, false).test();
    gateway.sendDecision(order, true).test();

    // Результат:
    inOrder.verify(stompClient)
        .send("/mobile/order", "{\"id\":\"7\", \"approved\":\"false\"}");
    inOrder.verify(stompClient)
        .send("/mobile/order", "{\"id\":\"7\", \"approved\":\"true\"}");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSendDecisionSuccess() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValue("");
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerSendDecisionError() {
    // Дано:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
    testObserver.assertNoValues();
  }
}