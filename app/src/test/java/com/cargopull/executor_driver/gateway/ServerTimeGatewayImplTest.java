package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.ServerTimeGateway;
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
public class ServerTimeGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ServerTimeGateway gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    gateway = new ServerTimeGatewayImpl(topicListener);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем топика */

  /**
   * Должен запросить у слушателя топика баланс исполнителя.
   */
  @Test
  public void askWebTopicListenerForExecutorBalance() {
    // Действие:
    gateway.loadServerTime().test();

    // Результат:
    verify(topicListener, only()).getAcknowledgedMessages();
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
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен игнорировать сообщение с заголовком с null.
   */
  @Test
  public void ignoreTimeStampHeaderWithNull() {
    // Дано:
    when(stompMessage.findHeader("ServerTimeStamp")).thenReturn(null);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку маппинга для нецифрового значения.
   */
  @Test
  public void answerMappingErrorForServerTimeStampHeaderWithWrongValue() {
    // Дано:
    when(stompMessage.findHeader("ServerTimeStamp")).thenReturn("a12345");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть текущие временные метки сервера для сообщения с заголовком 'ServerTimeStamp'.
   */
  @Test
  public void answerWithServerTimesForServerTimeStampHeader() {
    // Дано:
    when(stompMessage.findHeader("ServerTimeStamp")).thenReturn("12345");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime().test();

    // Результат:
    testSubscriber.assertValue(12345L);
  }

  /**
   * Должен вернуть ошибку маппинга для пустого значения.
   */
  @Test
  public void answerMappingErrorForServerTimeStampHeaderWithEmptyValue() {
    // Дано:
    when(stompMessage.findHeader("ServerTimeStamp")).thenReturn("");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(DataMappingException.class);
  }
}