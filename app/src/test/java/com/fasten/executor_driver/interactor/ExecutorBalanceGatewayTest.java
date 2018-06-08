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
import com.fasten.executor_driver.entity.ExecutorBalance;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.ExecutorBalanceGatewayImpl;
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
public class ExecutorBalanceGatewayTest {

  private ExecutorBalanceGateway executorBalanceGateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<String, ExecutorBalance> mapper;
  @Mock
  private ExecutorBalance executorBalance;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    executorBalanceGateway = new ExecutorBalanceGatewayImpl(stompClient, mapper);
    when(stompClient.topic(anyString())).thenReturn(Observable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP баланс исполнителя, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientForBalance() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForBalance() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP баланс исполнителя, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientForBalanceIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

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
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

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
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг если сообщение с заголовком Balance, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForBalanceHeaderIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(
                new StompHeader("Balance", "")
            ),
            "\n"
        )
    ));

    // Действие:
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    verify(mapper, only()).map("\n");
  }

  /**
   * Должен запросить маппинг после соединения если сообщение с верным заголовком Balance, если он не
   * соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForBalanceHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(
                new StompHeader("Balance", "")
            ),
            "\n"
        )
    ));

    // Действие:
    executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    verify(mapper, only()).map("\n");
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
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Balance, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForBalanceHeaderIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Balance", "payload")),
            null
        )
    ));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть статус для сообщения с заголовком Balance, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithBalanceForBalanceHeaderIfConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(executorBalance);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Balance", "payload")),
            "\n"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertValue(executorBalance);
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
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerConnectionErrorIfNotConnectingAfterConnected() {
    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

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
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой для сообщения с заголовком Balance, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForBalanceHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Balance", "")),
            "\n"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть статус для сообщения с верным заголовком Balance, если он не соединен и
   * соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForBalanceHeaderIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(executorBalance);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage(
            "MESSAGE",
            Collections.singletonList(new StompHeader("Balance", "")),
            "\n"
        )
    ));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertValue(executorBalance);
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
    TestSubscriber<ExecutorBalance> testSubscriber =
        executorBalanceGateway.loadExecutorBalance("1234567890").test();

    // Результат:
    testSubscriber.assertError(ConnectionClosedException.class);
    testSubscriber.assertNoValues();
  }
}