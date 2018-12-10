package com.cargopull.executor_driver.interactor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.OrderConfirmationGatewayImpl;
import io.reactivex.Completable;
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
public class OrderConfirmationGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderConfirmationGateway gateway;
  @Mock
  private ApiService apiService;
  @Mock
  private Mapper<Throwable, Throwable> errorMapper;
  @Mock
  private Order order;
  @Captor
  private ArgumentCaptor<ApiOrderDecision> orderDecisionCaptor;

  @Before
  public void setUp() {
    when(apiService.acceptOrderOffer(any())).thenReturn(Completable.never());
    gateway = new OrderConfirmationGatewayImpl(apiService, errorMapper);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendMessage() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(apiService);
    when(order.getId()).thenReturn(7L);

    // Действие:
    gateway.sendDecision(order, false).test().isDisposed();
    gateway.sendDecision(order, true).test().isDisposed();

    // Результат:
    inOrder.verify(apiService, times(2)).acceptOrderOffer(orderDecisionCaptor.capture());
    verifyNoMoreInteractions(apiService);
    assertEquals(orderDecisionCaptor.getAllValues().get(0).getId(), 7);
    assertFalse(orderDecisionCaptor.getAllValues().get(0).isApproved());
    assertEquals(orderDecisionCaptor.getAllValues().get(1).getId(), 7);
    assertTrue(orderDecisionCaptor.getAllValues().get(1).isApproved());
    verifyNoMoreInteractions(apiService);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSendDecisionSuccess() {
    // Дано:
    when(apiService.acceptOrderOffer(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValue("");
  }

  /**
   * Должен ответить спамленной ошибкой.
   */
  @Test
  public void answerSendDecisionError() throws Exception {
    // Дано:
    when(apiService.acceptOrderOffer(any()))
        .thenReturn(Completable.error(new IllegalArgumentException()));
    when(errorMapper.map(any())).thenReturn(new IllegalStateException());

    // Действие:
    TestObserver<String> testObserver = gateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalStateException.class);
    testObserver.assertNoValues();
  }
}