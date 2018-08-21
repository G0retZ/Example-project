package com.cargopull.executor_driver.interactor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.CancelOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class CancelOrderGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CancelOrderGateway gateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<StompMessage, List<CancelOrderReason>> mapper;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;
  @Captor
  private ArgumentCaptor<StompMessage> stompMessageCaptor;

  @Before
  public void setUp() {
    gateway = new CancelOrderGatewayImpl(stompClient, mapper);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.never());
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    when(stompClient.sendAfterConnection(any(StompMessage.class)))
        .thenReturn(Completable.complete());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP причины для отказа, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForCancelReason() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL);
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForCancelReason() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP причины для отказа, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForCancelReasonIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

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
  public void askStompClientToSendAckForCancelReasonIfConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("CancelReason", ""),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

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
  public void askStompClientToSendAckForCancelReasonIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Arrays.asList(
                    new StompHeader("CancelReason", ""),
                    new StompHeader("subscription", "subs"),
                    new StompHeader("message-id", "mess")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

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

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если сообщение без нужных заголовков, если он соединен и не соединяется.
   */
  @Test
  public void doNotTouchMapperIfWrongHeaderIfConnected() {
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
    gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если сообщение без нужных заголовков, если он не соединен и соединяется.
   */
  @Test
  public void doNotTouchMapperIfWrongHeaderIfConnectingAfterConnected() {
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
    gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком CancelReason, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForCancelReasonHeaderIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(
                    new StompHeader("CancelReason", "")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /**
   * Должен запросить маппинг после соединения если сообщение с верным заголовком CancelReason, если он не
   * соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForCancelReasonHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(
                    new StompHeader("CancelReason", "")
                ),
                "\n"
            )
        ));

    // Действие:
    gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }


  /**
   * Должен запросить у клиента STOMP отправку причины отказа.
   */
  @Test
  public void askStompClientToSendCancelOrderReason() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");

    // Действие:
    gateway.cancelOrder(cancelOrderReason).test();

    // Результат:
    verify(stompClient, only())
        .send("/mobile/takeOffOrder", "{\"id\":7,\"description\":\"seven\"}");
  }

  /* Проверяем результаты обработки сообщений от сервера по причинам для отказа */

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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой для сообщение с заголовком CancelReason, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForCancelReasonHeaderIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("CancelReason", "payload")),
                null
            )
        ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть причины для отказа для сообщения с заголовком CancelReason, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForCancelReasonHeaderIfConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    );
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("CancelReason", "payload")),
                "\n"
            )
        ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertValue(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    );
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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком CancelReason, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForCancelReasonHeaderIfConnectingAfterConnected()
      throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("CancelReason", "")),
                "\n"
            )
        ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть причины для отказа для сообщения с верным заголовком CancelReason, если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithCancelReasonsForCancelReasonHeaderIfConnectingAfterConnected()
      throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    );
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic("/queue/1234567890", StompClient.ACK_CLIENT_INDIVIDUAL))
        .thenReturn(Flowable.just(
            new StompMessage(
                "MESSAGE",
                Collections.singletonList(new StompHeader("CancelReason", "")),
                "\n"
            )
        ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertValue(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    );
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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        gateway.loadCancelOrderReasons("1234567890").test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCancelOrderReasonSuccess() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.cancelOrder(cancelOrderReason).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerCancelOrderReasonError() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.cancelOrder(cancelOrderReason).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}