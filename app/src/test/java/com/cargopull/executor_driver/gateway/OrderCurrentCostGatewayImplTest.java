package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.OrderCurrentCostGateway;
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
public class OrderCurrentCostGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderCurrentCostGateway gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private Mapper<StompMessage, Long> mapper;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    gateway = new OrderCurrentCostGatewayImpl(topicListener, mapper);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем топика */

  /**
   * Должен запросить у слушателя топика обновления цены.
   */
  @Test
  public void askWebTopicListenerForExecutorBalance() {
    // Действие:
    gateway.getOrderCurrentCost().test();

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
    gateway.getOrderCurrentCost().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком TotalAmount.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForTotalCost() throws Exception {
    // Дано:
    when(stompMessage.findHeader("TotalAmount")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    gateway.getOrderCurrentCost().test();

    // Результат:
    verify(mapper, only()).map(stompMessage);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.getOrderCurrentCost().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга для сообщения с заголовком TotalAmount.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForTotalAmountHeader() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(stompMessage);
    when(stompMessage.findHeader("TotalAmount")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.getOrderCurrentCost().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть число для сообщения с заголовком TotalAmount.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithExecutorBalanceForTotalAmountHeader() throws Exception {
    // Дано:
    when(mapper.map(stompMessage)).thenReturn(12345L);
    when(stompMessage.findHeader("TotalAmount")).thenReturn("payload");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.getOrderCurrentCost().test();

    // Результат:
    testSubscriber.assertValue(12345L);
    testSubscriber.assertNoErrors();
  }
}