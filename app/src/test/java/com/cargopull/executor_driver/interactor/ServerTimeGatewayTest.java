package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.ServerTimeGatewayImpl;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Collections;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class ServerTimeGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ServerTimeGateway gateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    gateway = new ServerTimeGatewayImpl(stompClient);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP текущие временные метки сервера, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForServerTime() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    gateway.loadServerTime("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForServerTime() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    gateway.loadServerTime("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP текущие временные метки сервера, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForServerTimeIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    gateway.loadServerTime("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
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
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен игнорировать сообщение с заголовком с null, если он соединен и не соединяется.
   */
  @Test
  public void ignoreTimeStampHeaderWithNullIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", null)),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть текущие временные метки сервера для сообщения с заголовком 'ServerTimeStamp',
   * если он соединен и не соединяется.
   */
  @Test
  public void answerWithServerTimesForServerTimeStampHeaderIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", "12345")),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertValue(12345L);
  }

  /**
   * Должен вернуть ошибку маппинга для нецифрового значения, если он соединен и не соединяется.
   */
  @Test
  public void answerMappingErrorForServerTimeStampHeaderWithWrongValueIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", "a12345")),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть ошибку маппинга для пустого значения, если он соединен и не соединяется.
   */
  @Test
  public void answerMappingErrorForServerTimeStampHeaderWithEmptyValueIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", "")),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(DataMappingException.class);
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
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

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
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

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
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен игнорировать сообщение с заголовком с null, если он не соединен и соединяется.
   */
  @Test
  public void ignoreTimeStampHeaderWithNullIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", null)),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть текущие временные метки сервера для сообщения с заголовком 'ServerTimeStamp',
   * если он не соединен и соединяется.
   */
  @Test
  public void answerWithServerTimesForServerTimeStampHeaderIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", "54321")),
                "Message this"
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertValue(54321L);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку маппинга для нецифрового значения, если он не соединен и соединяется.
   */
  @Test
  public void answerMappingErrorForServerTimeStampHeaderWithWrongValueIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", "a12345")),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть ошибку маппинга для пустого значения, если он не соединен и соединяется.
   */
  @Test
  public void answerMappingErrorForServerTimeStampHeaderWithEmptyValueIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("ServerTimeStamp", "")),
                "\"Message this\n\""
            )
        ));

    // Действие:
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(DataMappingException.class);
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
    TestSubscriber<Long> testSubscriber = gateway.loadServerTime("1234567890").test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
  }
}