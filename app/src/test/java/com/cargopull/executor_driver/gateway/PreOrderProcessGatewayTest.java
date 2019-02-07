package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderProcessGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderConfirmationGateway gateway;
  @Mock
  private ApiService apiService;
  @Mock
  private Order order;
  @Mock
  private ApiSimpleResult apiSimpleResult;
  @Captor
  private ArgumentCaptor<ApiOrderDecision> orderDecisionCaptor;

  @Before
  public void setUp() {
    when(apiService.sendPreOrderProcess(any())).thenReturn(Single.never());
    gateway = new PreOrderProcessGateway(apiService);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askApiServiceToSendDecisions() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(apiService);
    when(order.getId()).thenReturn(7L);

    // Действие:
    gateway.sendDecision(order, false).test().isDisposed();
    gateway.sendDecision(order, true).test().isDisposed();

    // Результат:
    inOrder.verify(apiService, times(2)).sendPreOrderProcess(orderDecisionCaptor.capture());
    verifyNoMoreInteractions(apiService);
    assertEquals(orderDecisionCaptor.getAllValues().get(0).getId(), 7);
    assertFalse(orderDecisionCaptor.getAllValues().get(0).isApproved());
    assertEquals(orderDecisionCaptor.getAllValues().get(1).getId(), 7);
    assertTrue(orderDecisionCaptor.getAllValues().get(1).isApproved());
    verifyNoMoreInteractions(apiService);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен ответить успехом, если заказ забронирован.
   */
  @Test
  public void answerSendDecisionServerSuccess() {
    // Дано:
    when(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult));
    when(apiSimpleResult.getCode()).thenReturn("200");
    when(apiSimpleResult.getMessage()).thenReturn("message");

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValue("message");
  }

  /**
   * Должен ответить успехом, если заказ не забронирован.
   */
  @Test
  public void answerSendDecisionServerError() {
    // Дано:
    when(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult));
    when(apiSimpleResult.getCode()).thenReturn("409");
    when(apiSimpleResult.getMessage()).thenReturn("error");

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(OrderConfirmationFailedException.class);
    testObserver.assertNoValues();
    assertEquals(testObserver.errors().size(), 1);
    assertEquals(testObserver.errors().get(0).getMessage(), "error");
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerSendDecisionError() {
    // Дано:
    when(apiService.sendPreOrderProcess(any()))
        .thenReturn(Single.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
    testObserver.assertNoValues();
  }
}