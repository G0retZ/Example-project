package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.ExecutorStateGatewayImpl;
import com.fasten.executor_driver.gateway.Mapper;
import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Collections;
import org.junit.Before;
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
public class ExecutorStateGatewayTest {

  private ExecutorStateGateway executorStateGateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<StompMessage, ExecutorState> mapper;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    executorStateGateway = new ExecutorStateGatewayImpl(stompClient, mapper);
    when(stompClient.topic(anyString())).thenReturn(Observable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP статусы, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForStatus() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForStatus() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP статусы, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForStatusIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890");
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
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE", null, "SHIFT"),
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "State")),
            "SHIFT"
        )
    ));

    // Действие:
    executorStateGateway.getState("1234567890").test();

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
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE", null, "SHIFT"),
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "State")),
            "SHIFT"
        )
    ));

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком Type="Status", если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForTypeHeaderIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(
                new StompHeader("Type", "Status")
            ),
            "payload"
        )
    ));

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком Status, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForStatusHeaderIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(
                new StompHeader("Status", "payload")
            ),
            "\n"
        )
    ));

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /**
   * Должен запросить маппинг после соединения если сообщение с заголовком Type = "Status" и верным
   * телом, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForTypeHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(
                new StompHeader("Type", "Status")
            ),
            "payload"
        )
    ));

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /**
   * Должен запросить маппинг после соединения если сообщение с верным заголовком Status, если он не
   * соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForStatusHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(
                new StompHeader("Status", "payload")
            ),
            "\n"
        )
    ));

    // Действие:
    executorStateGateway.getState("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
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
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE", null, "SHIFT"),
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "State")),
            "SHIFT"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Type="Status", если он соединен и не
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForTypeHeaderIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "Status")),
            null
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен ответить ошибкой для сообщение с заголовком Status, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForStatusHeaderIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Status", "payload")),
            null
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть статус для сообщения с заголовком Type = "Status", если он соединен и не
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForTypeHeaderIfConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(ExecutorState.SHIFT_OPENED);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "Status")),
            null
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertValue(ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен вернуть статус для сообщение с заголовком Status, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForStatusHeaderIfConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(ExecutorState.SHIFT_OPENED);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Status", "payload")),
            "\n"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertValue(ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   */
  @Test
  public void answerErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.error(new NoNetworkException()));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerConnectionErrorIfNotConnectingAfterConnected() {
    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

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
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE", null, "SHIFT"),
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "State")),
            "SHIFT"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Type = "Status", если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForTypeHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "Status")),
            null
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Status, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForStatusHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Status", "")),
            "\n"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть статус для сообщения с заголовком Type = "Status", если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForTypeHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(ExecutorState.SHIFT_OPENED);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Type", "Status")),
            "payload"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertValue(ExecutorState.SHIFT_OPENED);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть статус для сообщения с верным заголовком Status, если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForStatusHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(ExecutorState.SHIFT_OPENED);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Status", "")),
            "\n"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertValue(ExecutorState.SHIFT_OPENED);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется.
   */
  @Test
  public void answerErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString()))
        .thenReturn(Observable.error(new ConnectionClosedException()));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(ConnectionClosedException.class);
    testSubscriber.assertNoValues();
  }
}