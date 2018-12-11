package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.gateway.OrderRouteGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderRouteGateway gateway;
  @Mock
  private ApiService apiService;
  @Mock
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    when(apiService.setOrderStatus(anyString())).thenReturn(Completable.never());
    when(apiService.changeRoutePoint(anyString())).thenReturn(Completable.never());
    gateway = new OrderRouteGatewayImpl(apiService);
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
    gateway.closeRoutePoint(routePoint).test().isDisposed();

    // Результат:
    verify(apiService, only()).changeRoutePoint("{\"complete\":\"7\"}");
  }

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendCompleteTheOrderMessage() {
    // Действие:
    gateway.completeTheOrder().test().isDisposed();

    // Результат:
    verify(apiService, only()).setOrderStatus("\"COMPLETE_ORDER\"");
  }

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendNextRoutePointMessage() {
    // Дано:
    when(routePoint.getId()).thenReturn(7L);

    // Действие:
    gateway.nextRoutePoint(routePoint).test().isDisposed();

    // Результат:
    verify(apiService, only()).changeRoutePoint("{\"next\":\"7\"}");
  }

  /* Проверяем результаты отправки сообщений */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSendCloseRoutePointSuccess() {
    // Дано:
    when(apiService.changeRoutePoint(anyString())).thenReturn(Completable.complete());

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
    when(apiService.setOrderStatus(anyString())).thenReturn(Completable.complete());

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
    when(apiService.changeRoutePoint(anyString())).thenReturn(Completable.complete());

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
    when(apiService.changeRoutePoint(anyString()))
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
    when(apiService.setOrderStatus(anyString()))
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
    when(apiService.changeRoutePoint(anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.nextRoutePoint(routePoint).test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}