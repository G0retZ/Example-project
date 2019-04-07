package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderRouteUseCaseImpl useCase;

  @Mock
  private OrderUseCase orderUseCase;
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
    when(orderUseCase.getOrders()).thenReturn(Flowable.never());
    when(orderRouteGateway.closeRoutePoint(any())).thenReturn(Completable.never());
    when(orderRouteGateway.completeTheOrder()).thenReturn(Completable.never());
    when(orderRouteGateway.nextRoutePoint(any())).thenReturn(Completable.never());
    useCase = new OrderRouteUseCaseImpl(orderUseCase, orderRouteGateway);
  }

  /* Проверяем работу с юзкейсом заказа */

  /**
   * Должен запросить у юзкейсом получение выполняемого заказа только 1 раз.
   */
  @Test
  public void askGatewayForOrders() {
    // Действие:
    useCase.getOrderRoutePoints().test().isDisposed();
    useCase.getOrderRoutePoints().test().isDisposed();
    useCase.getOrderRoutePoints().test().isDisposed();
    useCase.getOrderRoutePoints().test().isDisposed();

    // Результат:
    verify(orderUseCase, only()).getOrders();
  }

  /* Проверяем работу с гейтвеем маршрута заказа */

  /**
   * Не должен трогать гейтвей.
   */
  @Test
  public void doNotTouchGateway() {
    // Действие:
    useCase.updateWith(Arrays.asList(routePoint, routePoint1, routePoint2, routePoint4));

    // Результат:
    verifyZeroInteractions(orderRouteGateway);
  }

  /**
   * Должен запросить у гейтвея отметить точку.
   */
  @Test
  public void askGatewayToCheckRoutePoint() {
    // Действие:
    useCase.closeRoutePoint(routePoint).test().isDisposed();

    // Результат:
    verify(orderRouteGateway, only()).closeRoutePoint(routePoint);
  }

  /**
   * Должен запросить у гейтвея завершить заказ.
   */
  @Test
  public void askGatewayToCompleteTheOrder() {
    // Действие:
    useCase.completeTheOrder().test().isDisposed();

    // Результат:
    verify(orderRouteGateway, only()).completeTheOrder();
  }

  /**
   * Должен запросить у гейтвея выбрать другую точку.
   */
  @Test
  public void askGatewayToUseNextRoutePoint() {
    // Действие:
    useCase.nextRoutePoint(routePoint).test().isDisposed();

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
    when(orderUseCase.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = useCase.getOrderRoutePoints().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить маршрутами до завершения.
   */
  @Test
  public void answerWithOrdersBeforeComplete() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order, order2));
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint1, routePoint2, routePoint3));
    when(order2.getRoutePath()).thenReturn(Arrays.asList(routePoint4, routePoint, routePoint3));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = useCase.getOrderRoutePoints().test();
    useCase.updateWith(Arrays.asList(routePoint4, routePoint3));
    useCase.updateWith(Arrays.asList(routePoint, routePoint2));

    // Результат:
    test.assertValueCount(2);
    test.assertValueAt(0, Arrays.asList(routePoint1, routePoint2, routePoint3));
    test.assertValueAt(1, Arrays.asList(routePoint4, routePoint, routePoint3));
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить маршрутами.
   */
  @Test
  public void answerWithAllOrders() {
    // Дано:
    when(orderUseCase.getOrders())
        .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()));
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint1, routePoint2, routePoint3));
    when(order2.getRoutePath()).thenReturn(Arrays.asList(routePoint4, routePoint, routePoint3));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = useCase.getOrderRoutePoints().test();
    useCase.updateWith(Arrays.asList(routePoint4, routePoint3));
    useCase.updateWith(Arrays.asList(routePoint, routePoint2));

    // Результат:
    test.assertValueCount(4);
    test.assertValueAt(0, Arrays.asList(routePoint1, routePoint2, routePoint3));
    test.assertValueAt(1, Arrays.asList(routePoint4, routePoint, routePoint3));
    test.assertValueAt(2, Arrays.asList(routePoint4, routePoint3));
    test.assertValueAt(3, Arrays.asList(routePoint, routePoint2));
    test.assertNotComplete();
    test.assertNoErrors();
  }

  /* Проверяем ответы на запросы управления точками */

  /**
   * Должен ответить ошибкой сети на запрос закрытия точки.
   */
  @Test
  public void answerNoNetworkErrorForCloseRoutePoint() {
    // Дано:
    when(orderRouteGateway.closeRoutePoint(any()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = useCase.closeRoutePoint(routePoint).test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на запрос завершения заказа.
   */
  @Test
  public void answerNoNetworkErrorForCompleteTheOrder() {
    // Дано:
    when(orderRouteGateway.completeTheOrder())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = useCase.completeTheOrder().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой сети на на запрос выбора другой точки.
   */
  @Test
  public void answerNoNetworkErrorForUseNextRoutePoint() {
    // Дано:
    when(orderRouteGateway.nextRoutePoint(any()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = useCase.nextRoutePoint(routePoint).test();

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
    TestObserver<Void> test = useCase.closeRoutePoint(routePoint).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить успехом запроса завершения заказа.
   */
  @Test
  public void answerSendCompleteTheOrderSuccessful() {
    // Дано:
    when(orderRouteGateway.completeTheOrder()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = useCase.completeTheOrder().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить успехом на запрос выбора другой точки.
   */
  @Test
  public void answerSendUseNextRoutePointSuccessful() {
    // Дано:
    when(orderRouteGateway.nextRoutePoint(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = useCase.nextRoutePoint(routePoint).test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}