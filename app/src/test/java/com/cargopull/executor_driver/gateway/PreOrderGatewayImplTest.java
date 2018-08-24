package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
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
public class PreOrderGatewayImplTest {

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
    gateway = new PreOrderGatewayImpl(topicListener, mapper);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем топика */

  /**
   * Должен запросить у слушателя топика предзаказы для исполнителя.
   */
  @Test
  public void askStompClientForPreOrders() {
    // Действие:
    gateway.getOrders().test();

    // Результат:
    verify(topicListener, only()).getAcknowledgedMessages();
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если сообщение без нужных заголовков.
   */
  @Test
  public void doNotTouchMapperIfWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком Preliminary.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForPreliminaryHeader() throws Exception {
    // Дано:
    when(stompMessage.findHeader("Preliminary")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getOrders().test();

    // Результат:
    verify(mapper, only()).map(stompMessage);
  }

  /* Проверяем результаты обработки сообщений от сервера по предзаказу */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга для сообщения с заголовком Preliminary.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForPreliminaryHeader() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(stompMessage);
    when(stompMessage.findHeader("Preliminary")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть предзаказ для сообщения с заголовком Preliminary.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithPreOrderForPreliminaryHeader() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn(order);
    when(stompMessage.findHeader("Preliminary")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Order> testSubscriber = gateway.getOrders().test();

    // Результат:
    testSubscriber.assertValue(order);
    testSubscriber.assertNoErrors();
  }
}