package com.cargopull.executor_driver.gateway;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.UpdateMessageGateway;
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
public class UpdateMessageGatewayImplTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private UpdateMessageGateway gateway;
  @Mock
  private TopicListener topicListener;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    gateway = new UpdateMessageGatewayImpl(topicListener);
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с слушателем топика */

  /**
   * Должен запросить у слушателя топика сообщения о новой версии приложения, если он соединен и не соединяется.
   */
  @Test
  public void askWebTopicListenerForUpdateMessages() {
    // Действие:
    gateway.loadUpdateMessages().test();

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
    TestSubscriber<String> testSubscriber = gateway.loadUpdateMessages().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть сообщения о новой версии приложения для сообщения с заголовком message = 'UpdateVersion'.
   */
  @Test
  public void answerWithUpdateMessagesForUpdateVersionHeaderIfConnected() {
    // Дано:
    when(stompMessage.findHeader("message")).thenReturn("UpdateVersion");
    when(stompMessage.getPayload()).thenReturn("\n\"Message this\"");
    when(topicListener.getAcknowledgedMessages()).thenReturn(Flowable.just(stompMessage));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway.loadUpdateMessages().test();

    // Результат:
    testSubscriber.assertValue("Message this");
    testSubscriber.assertNoErrors();
  }
}