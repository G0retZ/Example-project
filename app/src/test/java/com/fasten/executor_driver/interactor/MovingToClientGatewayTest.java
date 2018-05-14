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
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.MovingToClientGatewayImpl;
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
public class MovingToClientGatewayTest {

  private MovingToClientGateway orderGateway;
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
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
    orderGateway = new MovingToClientGatewayImpl(executorStateUseCase, stompClient, mapper);
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
   * Должен запросить у клиента STOMP отправку "звонок клиенту", если он соединен и не соединяется.
   */
  @Test
  public void askStompClientToSendCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    orderGateway.callToClient().test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).send("/mobile/trip", "\"CALL_TO_CLIENT\"");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку "я на месте", если он соединен и не соединяется.
   */
  @Test
  public void askStompClientToSendReportArrival() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnected()).thenReturn(true);

    // Действие:
    orderGateway.reportArrival().test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).send("/mobile/trip", "\"DRIVER_ARRIVED\"");
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
    orderGateway.callToClient().test();
    orderGateway.reportArrival().test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку "звонок клиенту", если он не соединен и соединяется.
   */
  @Test
  public void askStompClientToSendCallToClientIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    orderGateway.callToClient().test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).send("/mobile/trip", "\"CALL_TO_CLIENT\"");
    verifyNoMoreInteractions(stompClient);
  }

  /**
   * Должен запросить у клиента STOMP отправку "я на месте", если он не соединен и соединяется.
   */
  @Test
  public void askStompClientToSendReportArrivalIfConnecting() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(stompClient);
    when(stompClient.isConnecting()).thenReturn(true);

    // Действие:
    orderGateway.reportArrival().test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient).send("/mobile/trip", "\"DRIVER_ARRIVED\"");
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
   * Не должен трогать маппер, если пришел статус "ожидание подтверждения клиента".
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
   * Не должен трогать маппер, если пришел статус "получение заказа".
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
   * Не должен трогать маппер, если статус без сообщения.
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
   * Должен запросить маппинг для данных статуса.
   *
   * @throws Exception error
   */
  @Test
  public void askForMappingForData() throws Exception {
    // Дано:
    ExecutorState.MOVING_TO_CLIENT.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

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
   * Должен ответить ошибкой отсутствия заказов для статуса "принятие заказа".
   */
  @Test
  public void ignoreForDriverOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

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
   * Должен ответить ошибкой отсутствия заказов для статуса "на пути к клиенту" без данных.
   */
  @Test
  public void answerNoOrdersAvailableForNoData() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

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
    ExecutorState.MOVING_TO_CLIENT.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

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
    ExecutorState.MOVING_TO_CLIENT.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

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
  public void answerCallToClientSuccessIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.callToClient().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   */
  @Test
  public void answerReportArrivalSuccessIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.reportArrival().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   */
  @Test
  public void answerCallToClientErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.callToClient().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   */
  @Test
  public void answerReportArrivalErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.reportArrival().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerCallToClientErrorIfNotConnectedAndNotConnecting() {
    // Действие:
    TestObserver<Void> testObserver = orderGateway.callToClient().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerReportArrivalErrorIfNotConnectedAndNotConnecting() {
    // Действие:
    TestObserver<Void> testObserver = orderGateway.reportArrival().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется.
   */
  @Test
  public void answerCallToClientSuccessIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.callToClient().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется.
   */
  @Test
  public void answerReportArrivalSuccessIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderGateway.reportArrival().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется.
   */
  @Test
  public void answerCallToClientErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new ConnectionClosedException()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.callToClient().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется.
   */
  @Test
  public void answerReportArrivalErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new ConnectionClosedException()));

    // Действие:
    TestObserver<Void> testObserver = orderGateway.reportArrival().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }
}