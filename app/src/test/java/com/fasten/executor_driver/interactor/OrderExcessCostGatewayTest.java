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
import com.fasten.executor_driver.gateway.OrderExcessCostGatewayImpl;
import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class OrderExcessCostGatewayTest {

  private OrderExcessCostGateway executorStateGateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<StompMessage, Integer> mapper;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    executorStateGateway = new OrderExcessCostGatewayImpl(stompClient, mapper);
    when(stompClient.topic(anyString())).thenReturn(Observable.never());
  }

//  /* Проверяем работу с клиентом STOMP */
//
//  /**
//   * Должен запросить у клиента STOMP обновления цены, если он соединен и не соединяется.
//   */
//  @SuppressWarnings("SpellCheckingInspection")
//  @Test
//  public void askStompClientForExcessiveCost() {
//    // Дано:
//    InOrder inOrder = Mockito.inOrder(stompClient);
//    when(stompClient.isConnected()).thenReturn(true);
//
//    // Действие:
//    executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    inOrder.verify(stompClient).isConnected();
//    inOrder.verify(stompClient).topic("/mobile/order/ecost");
//    verifyNoMoreInteractions(stompClient);
//  }
//
//  /**
//   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
//   */
//  @Test
//  public void doNotAskStompClientToConnectOrForExcessiveCost() {
//    // Дано:
//    InOrder inOrder = Mockito.inOrder(stompClient);
//
//    // Действие:
//    executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    inOrder.verify(stompClient).isConnected();
//    inOrder.verify(stompClient).isConnecting();
//    verifyNoMoreInteractions(stompClient);
//  }
//
//  /**
//   * Должен запросить у клиента STOMP обновления цены, если он не соединен и соединяется.
//   */
//  @SuppressWarnings("SpellCheckingInspection")
//  @Test
//  public void askStompClientForExcessiveCostIfConnecting() {
//    // Дано:
//    InOrder inOrder = Mockito.inOrder(stompClient);
//    when(stompClient.isConnecting()).thenReturn(true);
//
//    // Действие:
//    executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    inOrder.verify(stompClient).isConnected();
//    inOrder.verify(stompClient).isConnecting();
//    inOrder.verify(stompClient).topic("/mobile/order/ecost");
//    verifyNoMoreInteractions(stompClient);
//  }
//
//  /* Проверяем работу с маппером */
//
//  /**
//   * Должен запросить маппинг, если он соединен и не соединяется.
//   *
//   * @throws Exception error
//   */
//  @Test
//  public void askForMappingForExcessiveCostIfConnected() throws Exception {
//    // Дано:
//    when(stompClient.isConnected()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.just(
//        new StompMessage("MESSAGE", new ArrayList<>(), "\n")
//    ));
//
//    // Действие:
//    executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    verify(mapper, only()).map(any());
//  }
//
//  /**
//   * Должен запросить маппинг после соединения, если он не соединен и соединяется.
//   *
//   * @throws Exception error
//   */
//  @Test
//  public void askForMappingForExcessiveCostIfConnectingAfterConnected() throws Exception {
//    // Дано:
//    when(stompClient.isConnecting()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.just(
//        new StompMessage("MESSAGE", new ArrayList<>(), "\n")
//    ));
//
//    // Действие:
//    executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    verify(mapper, only()).map(any());
//  }
//
//  /* Проверяем правильность потоков (добавить) */
//
//  /* Проверяем результаты обработки сообщений от сервера */
//
//  /**
//   * Должен ответить ошибкой маппинга, если он соединен и не соединяется.
//   *
//   * @throws Exception error
//   */
//  @Test
//  public void answerDataMappingErrorForExcessiveCostIfConnected() throws Exception {
//    // Дано:
//    doThrow(new DataMappingException()).when(mapper).map(any());
//    when(stompClient.isConnected()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.just(
//        new StompMessage("MESSAGE", new ArrayList<>(), "\n")
//    ));
//
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertError(DataMappingException.class);
//  }
//
//  /**
//   * Должен вернуть число, если он соединен и не соединяется.
//   *
//   * @throws Exception error
//   */
//  @Test
//  public void answerShiftOpenedForExcessiveCostIfConnected() throws Exception {
//    // Дано:
//    when(mapper.map(any())).thenReturn(12345);
//    when(stompClient.isConnected()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.just(
//        new StompMessage("MESSAGE", new ArrayList<>(), "\n")
//    ));
//
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertValue(12345);
//  }
//
//  /**
//   * Должен ответить ошибкой, если он соединен и не соединяется.
//   */
//  @Test
//  public void answerErrorIfConnected() {
//    // Дано:
//    when(stompClient.isConnected()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.error(new NoNetworkException()));
//
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertError(NoNetworkException.class);
//  }
//
//  /**
//   * Должен ответить ошибкой, если он не соединен и не соединяется.
//   */
//  @Test
//  public void answerConnectionErrorIfNotConnectingAfterConnected() {
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertError(ConnectionClosedException.class);
//  }
//
//  /**
//   * Должен ответить ошибкой маппинга, если он не соединен и соединяется.
//   *
//   * @throws Exception error
//   */
//  @Test
//  public void answerDataMappingErrorForExcessiveCostIfConnectingAfterConnected() throws Exception {
//    // Дано:
//    doThrow(new DataMappingException()).when(mapper).map(any());
//    when(stompClient.isConnecting()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.just(
//        new StompMessage("MESSAGE", new ArrayList<>(), "\n")
//    ));
//
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertError(DataMappingException.class);
//    testSubscriber.assertNoValues();
//  }
//
//  /**
//   * Должен вернуть число, если он не соединен и соединяется.
//   *
//   * @throws Exception error
//   */
//  @Test
//  public void answerShiftOpenedForExcessiveCostIfConnectingAfterConnected() throws Exception {
//    // Дано:
//    when(mapper.map(any())).thenReturn(54321);
//    when(stompClient.isConnecting()).thenReturn(true);
//    when(stompClient.topic(anyString())).thenReturn(Observable.just(
//        new StompMessage("MESSAGE", new ArrayList<>(), "\n")
//    ));
//
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertValue(54321);
//    testSubscriber.assertNoErrors();
//  }
//
//  /**
//   * Должен ответить ошибкой, если он не соединен и соединяется.
//   */
//  @Test
//  public void answerErrorIfConnecting() {
//    // Дано:
//    when(stompClient.isConnecting()).thenReturn(true);
//    when(stompClient.topic(anyString()))
//        .thenReturn(Observable.error(new ConnectionClosedException()));
//
//    // Действие:
//    TestSubscriber<Integer> testSubscriber = executorStateGateway.getOrderExcessCost().test();
//
//    // Результат:
//    testSubscriber.assertError(ConnectionClosedException.class);
//    testSubscriber.assertNoValues();
//  }
}