package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderGateway;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class OrderGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderGateway gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompMessage, Order> mapper;
  @Mock
  private StompMessage stompMessage;
  @Mock
  private Order order;

  @Before
  public void setUp() {
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика текущий заказ.
   */
  @Test
  public void askWebTopicListenerForOrder() {
    // Дано:
    gateway = new OrderGatewayImpl(topicListener, ExecutorState.DRIVER_ORDER_CONFIRMATION, mapper);

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verify(topicListener, only()).getAcknowledgedMessages();
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если статус не соответствует фильтруемому.
   */
  @Test
  public void doNotTouchMapperIfExecutorStateIncorrect() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("SHIFT_CLOSED", "SHIFT_OPENED",
        "ONLINE", "CLIENT_ORDER_CONFIRMATION", "MOVING_TO_CLIENT", "WAITING_FOR_CLIENT",
        "ORDER_FULFILLMENT", "PAYMENT_CONFIRMATION");
    gateway = new OrderGatewayImpl(topicListener, ExecutorState.DRIVER_ORDER_CONFIRMATION, mapper);
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage, stompMessage, stompMessage, stompMessage,
            stompMessage, stompMessage, stompMessage, stompMessage));

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг для данных статуса.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForData() throws Exception {
    // Дано:
    gateway = new OrderGatewayImpl(topicListener, ExecutorState.MOVING_TO_CLIENT, mapper);
    when(stompMessage.findHeader("Status")).thenReturn("MOVING_TO_CLIENT");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verify(mapper, only()).map(stompMessage);
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен игнорировать не свои статусы.
   */
  @Test
  public void ignoreForIncorrectExecutorState() {
    // Дано:
    gateway = new OrderGatewayImpl(topicListener, ExecutorState.DRIVER_ORDER_CONFIRMATION, mapper);
    when(stompMessage.findHeader("Status")).thenReturn("SHIFT_CLOSED", "SHIFT_OPENED",
        "ONLINE", "CLIENT_ORDER_CONFIRMATION", "MOVING_TO_CLIENT", "WAITING_FOR_CLIENT",
        "ORDER_FULFILLMENT", "PAYMENT_CONFIRMATION");
    when(topicListener.getAcknowledgedMessages())
        .thenReturn(Flowable.just(stompMessage, stompMessage, stompMessage, stompMessage,
            stompMessage, stompMessage, stompMessage, stompMessage));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    gateway = new OrderGatewayImpl(topicListener, ExecutorState.MOVING_TO_CLIENT, mapper);
    doThrow(new DataMappingException()).when(mapper).map(stompMessage);
    when(stompMessage.findHeader("Status")).thenReturn("MOVING_TO_CLIENT");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть заказ.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithOrder() throws Exception {
    // Дано:
    gateway = new OrderGatewayImpl(topicListener, ExecutorState.WAITING_FOR_CLIENT, mapper);
    when(mapper.map(stompMessage)).thenReturn(order);
    when(stompMessage.findHeader("Status")).thenReturn("WAITING_FOR_CLIENT");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber.assertValue(order);
  }
}