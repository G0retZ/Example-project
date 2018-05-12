package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyBoolean;
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
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.DriverOrderConfirmationGatewayImpl;
import com.fasten.executor_driver.gateway.Mapper;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class DriverOrderConfirmationGatewayTest {

  private OrderGateway orderGateway;
  @Mock
  private StompClient stompClient;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;
  @Mock
  private Mapper<String, Order> mapper;
  @Mock
  private Order order;

  @Before
  public void setUp() {
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    ExecutorState.DRIVER_ORDER_CONFIRMATION.setData(null);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
    orderGateway = new DriverOrderConfirmationGatewayImpl(executorStateUseCase, stompClient,
        mapper);
  }

  /* Проверяем работу с с юзкейсом статусов */

  /**
   * Должен попросить у юзкейса статусы исполнителя.
   */
  @Test
  public void askExecutorStateUseCaseForStatusUpdates() {
    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку, если он соединен и не соединяется.
   */
  @Test
  public void askStompClientToSendMessage() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);
    when(order.getId()).thenReturn(7L);

    // Действие:
    orderGateway.sendDecision(order, false).test();
    orderGateway.sendDecision(order, true).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient)
        .send("/mobile/order", "{\"id\":\"7\", \"approved\":\"false\"}");
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient)
        .send("/mobile/order", "{\"id\":\"7\", \"approved\":\"true\"}");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Не должен просить у клиента STOMP соединение, если он не соединен и не соединяется.
   */
  @Test
  public void doNotAskStompClientToConnectOrSendIfNotConnected() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);

    // Действие:
    orderGateway.sendDecision(order, false).test();
    orderGateway.sendDecision(order, true).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку, если он не соединен и соединяется.
   */
  @Test
  public void askStompClientToSendMessageIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);
    when(order.getId()).thenReturn(7L);

    // Действие:
    orderGateway.sendDecision(order, false).test();
    orderGateway.sendDecision(order, true).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient)
        .send("/mobile/order", "{\"id\":\"7\", \"approved\":\"false\"}");
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient)
        .send("/mobile/order", "{\"id\":\"7\", \"approved\":\"true\"}");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем работу с маппером */

  /**
   * Не должен трогать маппер, если не статус "смена закрыта".
   */
  @Test
  public void doNotTouchMapperIfShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если пришел статус "смена открыта".
   */
  @Test
  public void doNotTouchMapperIfShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если пришел статус "на линии".
   */
  @Test
  public void doNotTouchMapperIfOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если не пришел статус "ожидание подтверждения клиента".
   */
  @Test
  public void doNotTouchMapperIfWaitForClientConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если не пришел статус "На пути к клиенту".
   */
  @Test
  public void doNotTouchMapperIfMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Не должен трогать маппер, если статус без сообщения.
   */
  @Test
  public void doNotTouchMapperIfNoData() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verifyZeroInteractions(mapper);
  }

  /**
   * Должен запросить маппинг для данных статуса.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForData() throws Exception {
    // Дано:
    ExecutorState.DRIVER_ORDER_CONFIRMATION.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    orderGateway.getOrders().test();

    // Результат:
    verify(mapper, only()).map("");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "смена закрыта".
   */
  @Test
  public void ignoreForShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "смена открыта".
   */
  @Test
  public void ignoreForShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "онлайн".
   */
  @Test
  public void ignoreForOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "ожидание подтверждения клиента".
   */
  @Test
  public void ignoreForClientOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "на пути к клиенту".
   */
  @Test
  public void ignoreForMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой отсутствия заказов для статуса "принятие заказа" без данных.
   */
  @Test
  public void answerNoOrdersAvailableForNoData() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(NoOrdersAvailableException.class);
  }

  /**
   * Должен ответить ошибкой маппинга.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    doThrow(new DataMappingException()).when(mapper).map(anyString());
    ExecutorState.DRIVER_ORDER_CONFIRMATION.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть заказ.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithOrder() throws Exception {
    // Дано:
    when(mapper.map(anyString())).thenReturn(order);
    ExecutorState.DRIVER_ORDER_CONFIRMATION.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    TestSubscriber<Order> testSubscriber = orderGateway.getOrders().test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber.assertValue(order);
  }

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   */
  @Test
  public void answerSendDecisionSuccessIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   */
  @Test
  public void answerSendDecisionErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerSendDecisionErrorIfNotConnectedAndNotConnecting() {
    // Действие:
    TestObserver<Void> testObserver = orderGateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется.
   */
  @Test
  public void answerSendDecisionSuccessIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется.
   */
  @Test
  public void answerSendDecisionErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new ConnectionClosedException()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.sendDecision(order, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }
}