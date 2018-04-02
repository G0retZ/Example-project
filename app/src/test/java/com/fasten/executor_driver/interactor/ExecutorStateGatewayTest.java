package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
  private Mapper<String, ExecutorState> mapper;

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

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен игнорировать сообщение с неверным заголовком, если он соединен и не соединяется.
   */
  @Test
  public void ignoreWrongHeaderIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE", null, "SHIFT")
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой если сообщение с верным заголовком, но тело неверное, если он
   * соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map("payload");
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
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть статус если сообщение с верным заголовком c верным телом, если он соединен и не
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedIfConnected() throws Exception {
    // Дано:
    when(mapper.map("payload")).thenReturn(ExecutorState.SHIFT_OPENED);
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
   * Должен игнорировать сообщение с неверным заголовком, если он не соединен и соединяется.
   */
  @Test
  public void ignoreWrongHeaderIfConnectingAfterConnected() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE", null, "SHIFT")
    ));

    // Действие:
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой если сообщение с верным заголовком, но тело неверное, если он не
   * соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorIfConnectingAfterConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map("payload");
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
    TestSubscriber<ExecutorState> testSubscriber =
        executorStateGateway.getState("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть статус если сообщение с верным заголовком и верным телом, если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(mapper.map("payload")).thenReturn(ExecutorState.SHIFT_OPENED);
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