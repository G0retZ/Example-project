package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.MissedOrderGateway;
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
public class MissedOrderGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private MissedOrderGateway gateway;

  @Mock
  private TopicListener topicListener;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    gateway = new MissedOrderGatewayImpl(topicListener);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем сокета */

  /**
   * Должен запросить у слушателя топика баланс исполнителя.
   */
  @Test
  public void askWebTopicListenerForExecutorBalance() {
    // Действие:
    gateway.loadMissedOrdersMessages().test();

    // Результат:
    verify(topicListener, only()).getAcknowledgedMessages();
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен игнорировать сообщение без нужных заголовков.
   */
  @Test
  public void ignoreWrongHeader() {
    // Дано:
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.loadMissedOrdersMessages().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен вернуть сообщения об упущенных заказах для сообщения с заголовком MissedOrder.
   */
  @Test
  public void answerWithMissedOrderMessagesForMissedOrderHeader() {
    // Дано:
    when(stompMessage.findHeader("MissedOrder")).thenReturn("payload");
    when(stompMessage.getPayload()).thenReturn("Message this\n");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.loadMissedOrdersMessages().test();

    // Результат:
    testSubscriber.assertValue("Message this");
  }
}