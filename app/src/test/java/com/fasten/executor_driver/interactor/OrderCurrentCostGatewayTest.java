package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.OrderCurrentCostGatewayImpl;
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
public class OrderCurrentCostGatewayTest {

  private OrderCurrentCostGateway executorStateGateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<StompMessage, Integer> mapper;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    executorStateGateway = new OrderCurrentCostGatewayImpl(stompClient, mapper);
    when(stompClient.topic(anyString())).thenReturn(Observable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP обновления цены, если он соединен и не соединяется.
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void askStompClientForTotalCost() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).topic("/queue/1234567890");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrForTotalCost() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP обновления цены, если он не соединен и соединяется.
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void askStompClientForTotalCostIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).topic("/queue/1234567890");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем работу с маппером */

  /**
   * Должен запросить маппинг, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForTotalCostIfConnected() throws Exception {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("TotalAmount", "")), "\n")
    ));

    // Действие:
    executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /**
   * Должен запросить маппинг после соединения, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForTotalCostIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("TotalAmount", "")), "\n")
    ));

    // Действие:
    executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    verify(mapper, only()).map(any());
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты обработки сообщений от сервера */

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
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой маппинга, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForTotalCostIfConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("TotalAmount", "")), "\n")
    ));

    // Действие:
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть число, если он соединен и не соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForTotalCostIfConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(12345);
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("TotalAmount", "")), "\n")
    ));

    // Действие:
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertValue(12345);
  }

  /**
   * Должен игнорировать ошибку, если он соединен и не соединяется.
   */
  @Test
  public void ignoreErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.error(new NoNetworkException()));

    // Действие:
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

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
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

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
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой маппинга, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingErrorForTotalCostIfConnectingAfterConnected() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(any());
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("TotalAmount", "")), "\n")
    ));

    // Действие:
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть число, если он не соединен и соединяется.
   *
   * @throws Exception error
   */
  @Test
  public void answerShiftOpenedForTotalCostIfConnectingAfterConnected() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(54321);
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString())).thenReturn(Observable.just(
        new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("TotalAmount", "")), "\n")
    ));

    // Действие:
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertValue(54321);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен игнорировать ошибку, если он не соединен и соединяется.
   */
  @Test
  public void answerErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.topic(anyString()))
        .thenReturn(Observable.error(new ConnectionClosedException()));

    // Действие:
    TestSubscriber<Integer> testSubscriber =
        executorStateGateway.getOrderCurrentCost("1234567890").test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
  }
}