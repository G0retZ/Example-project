package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.gateway.DataMappingException;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteUseCaseTest {

  private OrderRouteUseCase orderRouteUseCase;

  @Mock
  private OrderGateway orderGateway;
  @Mock
  private OrderRouteGateway orderRouteGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;
  @Mock
  private RoutePoint routePoint3;
  @Mock
  private RoutePoint routePoint4;

  @Before
  public void setUp() {
    when(orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT)).thenReturn(Flowable.never());
    when(orderRouteGateway.closeRoutePoint(any())).thenReturn(Completable.never());
    when(orderRouteGateway.nextRoutePoint(any())).thenReturn(Completable.never());
    orderRouteUseCase = new OrderRouteUseCaseImpl(orderGateway, orderRouteGateway);
  }

  /* Проверяем работу с гейтвеем заказа */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Действие:
    orderRouteUseCase.getOrderRoutePoints().test();

    // Результат:
    verify(orderGateway, only()).getOrders(ExecutorState.ORDER_FULFILLMENT);
  }

  /* Проверяем работу с гейтвеем маршрута заказа */

  /**
   * Должен запросить у гейтвея отметить точку.
   */
  @Test
  public void askGatewayToCheckRoutePoint() {
    // Действие:
    orderRouteUseCase.closeRoutePoint(routePoint).test();

    // Результат:
    verify(orderRouteGateway, only()).closeRoutePoint(routePoint);
  }

  /**
   * Должен запросить у гейтвея снять отметку с точки.
   */
  @Test
  public void askGatewayToUnCheckRoutePoint() {
    // Действие:
    orderRouteUseCase.nextRoutePoint(routePoint).test();

    // Результат:
    verify(orderRouteGateway, only()).nextRoutePoint(routePoint);
  }

  /* Проверяем ответы на запрос маршрута */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = orderRouteUseCase.getOrderRoutePoints().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить маршрутами.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT))
        .thenReturn(Flowable.just(order, order2));
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint1, routePoint2, routePoint3));
    when(order2.getRoutePath()).thenReturn(Arrays.asList(routePoint4, routePoint, routePoint3));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = orderRouteUseCase.getOrderRoutePoints().test();

    // Результат:
    test.assertValueCount(2);
    test.assertValueAt(0, Arrays.asList(routePoint1, routePoint2, routePoint3));
    test.assertValueAt(1, Arrays.asList(routePoint4, routePoint, routePoint3));
    test.assertComplete();
    test.assertNoErrors();
  }

  /* Проверяем ответы на запросы открытия/закрытия точки */

  /**
   * Должен ответить ошибкой сети на запрос закрытия точки.
   */
  @Test
  public void answerNoNetworkErrorForCloseRoutePoint() {
    // Дано:
    when(orderRouteGateway.closeRoutePoint(any()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = orderRouteUseCase.closeRoutePoint(routePoint).test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на на запрос открытия точки.
   */
  @Test
  public void answerNoNetworkErrorForOpenRoutePoint() {
    // Дано:
    when(orderRouteGateway.nextRoutePoint(any()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = orderRouteUseCase.nextRoutePoint(routePoint).test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом запроса закрытия точки.
   */
  @Test
  public void answerSendCloseRoutePointSuccessful() {
    // Дано:
    when(orderRouteGateway.closeRoutePoint(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = orderRouteUseCase.closeRoutePoint(routePoint).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить успехом на запрос открытия точки.
   */
  @Test
  public void answerSendOpenRoutePointSuccessful() {
    // Дано:
    when(orderRouteGateway.nextRoutePoint(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = orderRouteUseCase.nextRoutePoint(routePoint).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}