package com.cargopull.executor_driver.interactor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.gateway.UpdateMessageGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class UpdateMessageGatewayTest {

  private UpdateMessageGateway gateway;

  @Mock
  private StompClient stompClient;
  @Captor
  private ArgumentCaptor<StompMessage> stompMessageCaptor;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    gateway = new UpdateMessageGatewayImpl(stompClient);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.never());
    when(stompClient.sendAfterConnection(any(StompMessage.class)))
        .thenReturn(Completable.complete());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP сообщения о новой версии приложения, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForUpdateMessages() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForUpdateMessages() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP сообщения о новой версии приложения, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForUpdateMessagesIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientToSendAckForUpdateMessagesIfConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("message", "UpdateVersion"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку ACK сразу, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientToSendAckForUpdateMessagesIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("message", "UpdateVersion"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    inOrder.verify(stompClient).sendAfterConnection(stompMessageCaptor.capture());
    assertEquals(stompMessageCaptor.getValue().getStompCommand(), "ACK");
    assertEquals(stompMessageCaptor.getValue().findHeader("subscription"), "subs");
    assertEquals(stompMessageCaptor.getValue().findHeader("message-id"), "mess");
    assertEquals(stompMessageCaptor.getValue().getPayload(), "");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен игнорировать сообщение без нужных заголовков, если он соединен и не соединяется.
   */
  @Test
  public void ignoreWrongHeaderIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage("MESSAGE", null, "SHIFT"),
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("Type", "State")),
                "SHIFT"
            )
        ));

    // Действие:
    TestSubscriber<String> testSubscriber = gateway
        .loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть сообщения о новой версии приложения для сообщения с заголовком message =
   * 'UpdateVersion', если он соединен и не соединяется.
   */
  @Test
  public void answerWithUpdateMessagesForUpdateVersionHeaderIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("message", "UpdateVersion")),
                "Message this\n"
            )
        ));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertValue("Message this");
  }

  /**
   * Должен игнорировать ошибку, если он соединен и не соединяется.
   */
  @Test
  public void ignoreErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.error(new NoNetworkException()));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerConnectionErrorIfNotConnectingAfterConnected() {
    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен игнорировать сообщение без нужных заголовков, если он не соединен и соединяется.
   */
  @Test
  public void ignoreWrongHeaderIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage("MESSAGE", null, "SHIFT"),
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("Type", "State")),
                "SHIFT"
            )
        ));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть сообщения о новой версии приложения для сообщения с верным заголовком UpdateMessage,
   * если он не соединен и соединяется.
   */
  @Test
  public void answerWithMessagesForUpdateMessageHeaderIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("message", "UpdateVersion")),
                "Message this\n"
            )
        ));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertValue("Message this");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен игнорировать ошибку, если он не соединен и соединяется.
   */
  @Test
  public void answerErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadUpdateMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
  }
}