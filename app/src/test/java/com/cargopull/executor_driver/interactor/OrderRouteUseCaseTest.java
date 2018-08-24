package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
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

  private OrderRouteUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderGateway orderGateway;
  @Mock
  private DataReceiver<String> loginReceiver;
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
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(orderGateway.getOrders()).thenReturn(Flowable.never());
    when(orderRouteGateway.closeRoutePoint(any())).thenReturn(Completable.never());
    when(orderRouteGateway.completeTheOrder()).thenReturn(Completable.never());
    when(orderRouteGateway.nextRoutePoint(any())).thenReturn(Completable.never());
    useCase = new OrderRouteUseCaseImpl(errorReporter, orderGateway, loginReceiver,
        orderRouteGateway);
  }

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    useCase.getOrderRoutePoints().test();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /* Проверяем работу с гейтвеем заказа */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getOrderRoutePoints().test();

    // Результат:
    verify(orderGateway, times(4)).getOrders();
    verifyNoMoreInteractions(orderGateway);
  }

  /* Проверяем работу с гейтвеем маршрута заказа */

  /**
   * Должен запросить у гейтвея отметить точку.
   */
  @Test
  public void askGatewayToCheckRoutePoint() {
    // Действие:
    useCase.closeRoutePoint(routePoint).test();

    // Результат:
    verify(orderRouteGateway, only()).closeRoutePoint(routePoint);
  }

  /**
   * Должен запросить у гейтвея завершить заказ.
   */
  @Test
  public void askGatewayToCompleteTheOrder() {
    // Действие:
    useCase.completeTheOrder().test();

    // Результат:
    verify(orderRouteGateway, only()).completeTheOrder();
  }

  /**
   * Должен запросить у гейтвея выбрать другую точку.
   */
  @Test
  public void askGatewayToUseNextRoutePoint() {
    // Действие:
    useCase.nextRoutePoint(routePoint).test();

    // Результат:
    verify(orderRouteGateway, only()).nextRoutePoint(routePoint);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders())
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrderRoutePoints().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы на запрос маршрута */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders())
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = useCase.getOrderRoutePoints().test();

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
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(orderGateway.getOrders()).thenReturn(Flowable.just(order, order2));
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint1, routePoint2, routePoint3));
    when(order2.getRoutePath()).thenReturn(Arrays.asList(routePoint4, routePoint, routePoint3));

    // Действие:
    TestSubscriber<List<RoutePoint>> test = useCase.getOrderRoutePoints().test();

    // Результат:
    test.assertValueCount(2);
    test.assertValueAt(0, Arrays.asList(routePoint1, routePoint2, routePoint3));
    test.assertValueAt(1, Arrays.asList(routePoint4, routePoint, routePoint3));
    test.assertComplete();
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