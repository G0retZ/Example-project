package com.cargopull.executor_driver.interactor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.gateway.MissedOrderGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.Collections;
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
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class MissedOrderGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private MissedOrderGateway gateway;

  @Mock
  private StompClient stompClient;
  @Captor
  private ArgumentCaptor<StompMessage> stompMessageCaptor;

  @Before
  public void setUp() {
    gateway = new MissedOrderGatewayImpl(stompClient);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.never());
    when(stompClient.sendAfterConnection(any(StompMessage.class)))
        .thenReturn(Completable.complete());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP сообщения об упущенных заказах, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForMissedOrderMessages() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    gateway.loadMissedOrdersMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForMissedOrderMessages() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    gateway.loadMissedOrdersMessages("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP сообщения об упущенных заказах, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForMissedOrderMessagesIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    gateway.loadMissedOrdersMessages("1234567890").test();

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
  public void askStompClientToSendAckForMissedOrderMessagesIfConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("MissedOrder", "payload"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadMissedOrdersMessages("1234567890").test();

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
  public void askStompClientToSendAckForMissedOrderMessagesIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("MissedOrder", "payload"),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadMissedOrdersMessages("1234567890").test();

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
        .loadMissedOrdersMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть сообщения об упущенных заказах для сообщения с заголовком MissedOrder, если он соединен и не соединяется.
   */
  @Test
  public void answerWithMissedOrderMessagesForMissedOrderHeaderIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("MissedOrder", "payload")),
                "Message this\n"
            )
        ));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadMissedOrdersMessages("1234567890").test();

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
        gateway.loadMissedOrdersMessages("1234567890").test();

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
        gateway.loadMissedOrdersMessages("1234567890").test();

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
        gateway.loadMissedOrdersMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть сообщения об упущенных заказах для сообщения с верным заголовком MissedOrder, если он не соединен и
   * соединяется.
   */
  @Test
  public void answerWithMessagesForMissedOrderHeaderIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("MissedOrder", "")),
                "Message this\n"
            )
        ));

    // Действие:
    TestSubscriber<String> testSubscriber =
        gateway.loadMissedOrdersMessages("1234567890").test();

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
        gateway.loadMissedOrdersMessages("1234567890").test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
  }
}