package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.gateway.OrderRouteGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteGatewayTest {

  private OrderRouteGateway gateway;
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
    gateway = new OrderRouteGatewayImpl(stompClient);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendCloseRoutePointMessage() {
    // Дано:
    when(routePoint.getId()).thenReturn(7L);

    // Действие:
    gateway.closeRoutePoint(routePoint).test();

    // Результат:
    verify(stompClient, only()).send("/mobile/changeRoutePoint", "{\"complete\":\"7\"}");
  }

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendCompleteTheOrderMessage() {
    // Действие:
    gateway.completeTheOrder().test();

    // Результат:
    verify(stompClient, only()).send("/mobile/trip", "\"COMPLETE_ORDER\"");
  }

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendNextRoutePointMessage() {
    // Дано:
    when(routePoint.getId()).thenReturn(7L);

    // Действие:
    gateway.nextRoutePoint(routePoint).test();

    // Результат:
    verify(stompClient, only()).send("/mobile/changeRoutePoint", "{\"next\":\"7\"}");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем результаты отправки сообщений */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSendCloseRoutePointSuccess() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.closeRoutePoint(routePoint).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSendCompleteTheOrderSuccess() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.completeTheOrder().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSendNextRoutePointSuccess() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.nextRoutePoint(routePoint).test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerSendCloseRoutePointError() {
    // Дано:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.closeRoutePoint(routePoint).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerSendCompleteTheOrderError() {
    // Дано:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.completeTheOrder().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerSendNextRoutePointError() {
    // Дано:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.nextRoutePoint(routePoint).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}