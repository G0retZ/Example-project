package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.gateway.OrderRouteGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteGatewayTest {

  private OrderRouteGateway orderRouteGateway;
  @Mock
  private StompClient stompClient;
  @Mock
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    orderRouteGateway = new OrderRouteGatewayImpl(stompClient);
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
    when(routePoint.getId()).thenReturn(7L);

    // Действие:
    orderRouteGateway.checkRoutePoint(routePoint, false).test();
    orderRouteGateway.checkRoutePoint(routePoint, true).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient)
        .send("/mobile/trip", "{\"id\":\"7\", \"checked\":\"false\"}");
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient)
        .send("/mobile/trip", "{\"id\":\"7\", \"checked\":\"true\"}");
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
    orderRouteGateway.checkRoutePoint(routePoint, false).test();
    orderRouteGateway.checkRoutePoint(routePoint, true).test();

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
    when(routePoint.getId()).thenReturn(7L);

    // Действие:
    orderRouteGateway.checkRoutePoint(routePoint, false).test();
    orderRouteGateway.checkRoutePoint(routePoint, true).test();

    // Результат:
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient)
        .send("/mobile/trip", "{\"id\":\"7\", \"checked\":\"false\"}");
    inOrder.verify(stompClient).isConnected();
    inOrder.verify(stompClient).isConnecting();
    inOrder.verify(stompClient)
        .send("/mobile/trip", "{\"id\":\"7\", \"checked\":\"true\"}");
    verifyNoMoreInteractions(stompClient);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить успехом, если он соединен и не соединяется.
   */
  @Test
  public void answerSendRoutePointCheckSuccessIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderRouteGateway.checkRoutePoint(routePoint, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он соединен и не соединяется.
   */
  @Test
  public void answerSendRoutePointCheckErrorIfConnected() {
    // Дано:
    when(stompClient.isConnected()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> testObserver = orderRouteGateway.checkRoutePoint(routePoint, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой, если он не соединен и не соединяется.
   */
  @Test
  public void answerSendRoutePointCheckErrorIfNotConnectedAndNotConnecting() {
    // Действие:
    TestObserver<Void> testObserver = orderRouteGateway.checkRoutePoint(routePoint, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }

  /**
   * Должен ответить успехом, если он не соединен и соединяется.
   */
  @Test
  public void answerSendRoutePointCheckSuccessIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = orderRouteGateway.checkRoutePoint(routePoint, false).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой, если он не соединен и соединяется.
   */
  @Test
  public void answerSendRoutePointCheckErrorIfConnecting() {
    // Дано:
    when(stompClient.isConnecting()).thenReturn(true);
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new ConnectionClosedException()));

    // Действие:
    TestObserver<Void> testObserver = orderRouteGateway.checkRoutePoint(routePoint, false).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(ConnectionClosedException.class);
  }
}