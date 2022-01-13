package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.gateway.DataMappingException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingPreOrderUseCaseImplTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private UpcomingPreOrderUseCaseImpl useCase;

  @Mock
  private CommonGateway<Order> gateway;
  @Mock
  private OrdersUseCase ordersUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;
  private FlowableEmitter<Order> orderEmitter;
  private FlowableEmitter<Set<Order>> ordersEmitter;

  @Before
  public void setUp() {
    when(order.withEtaToStartPoint(anyLong())).thenReturn(order3);
    when(order1.withEtaToStartPoint(anyLong())).thenReturn(order2);
    when(order2.withEtaToStartPoint(anyLong())).thenReturn(order1);
    when(order3.withEtaToStartPoint(anyLong())).thenReturn(order);
    when(gateway.getData())
        .thenReturn(Flowable.create(e -> orderEmitter = e, BackpressureStrategy.BUFFER));
    when(ordersUseCase.getOrdersSet())
        .thenReturn(Flowable.create(e -> ordersEmitter = e, BackpressureStrategy.BUFFER));
    useCase = new UpcomingPreOrderUseCaseImpl(gateway, ordersUseCase);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа только раз.
   */
  @Test
  public void askGatewayForOrdersOnlyOnce() {
    // Действие:
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем работу с юзкейсом списка заказов */

  /**
   * Не должен запросить у юзкейса списка получение заказов без полученных значений.
   */
  @Test
  public void doNotAskUseCaseForOrdersListForGet() {
    // Действие:
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();

    // Результат:
    verifyNoInteractions(ordersUseCase);
  }

  /**
   * Должен запросить у юзкейса списка получение заказов на каждый новый предстоящий предзаказ.
   */
  @Test
  public void askUseCaseForOrdersListForEveryValue() {
    // Действие:
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    orderEmitter.onNext(order);
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    orderEmitter.onNext(order2);
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();

    // Результат:
    verify(ordersUseCase, times(2)).getOrdersSet();
    verifyNoMoreInteractions(ordersUseCase);
  }

  /* Проверяем модификацию заказа из списка */

  /**
   * Должен запросить у элемента из списка модификацию ETA.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  public void askOrderToModifyEta() {
    // Дано:
    when(order1.getEtaToStartPoint()).thenReturn(1L);
    when(order2.getEtaToStartPoint()).thenReturn(2L);
    when(order3.getEtaToStartPoint()).thenReturn(3L);
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    useCase.getOrders().test().isDisposed();
    orderEmitter.onNext(order);
    orderEmitter.onNext(order2);
    orderEmitter.onNext(order3);
    orderEmitter.onNext(order1);

    // Результат:
    verify(order).withEtaToStartPoint(0L);
    verify(order).getEtaToStartPoint();
    verify(order1).withEtaToStartPoint(1L);
    verify(order1).getEtaToStartPoint();
    verify(order2).withEtaToStartPoint(2L);
    verify(order2).getEtaToStartPoint();
    verify(order3).withEtaToStartPoint(3L);
    verify(order3).getEtaToStartPoint();
    verifyNoMoreInteractions(order, order1, order2, order3);
  }

  /* Проверяем ответы */

  /**
   * Не должен ничем отвечать, если список не был получен.
   */
  @Test
  public void doNotAnswerForNoChoice() {
    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    orderEmitter.onNext(order);
    orderEmitter.onNext(order1);
    orderEmitter.onNext(order2);

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить предстоящими предзаказами из списка.
   */
  @Test
  public void answerWithSelectedOrders() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    orderEmitter.onNext(order);
    orderEmitter.onNext(order2);
    orderEmitter.onNext(order1);
    orderEmitter.onNext(order3);

    // Результат:
    testSubscriber.assertValues(order3, order1, order2, order);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить выбранным заказом, пока он есть в списке.
   */
  @Test
  public void answerWithSelectedOrder() {
    // Дано:
    when(order1.withEtaToStartPoint(anyLong()))
        .thenReturn(order, order1, order, order3, order2, order);

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    orderEmitter.onNext(order1);
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order3, order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order3, order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order1, order3)));

    // Результат:
    testSubscriber.assertValues(order, order1, order, order3, order2, order);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку полуения предстоящего предзаказа.
   */
  @Test
  public void answerErrorOnUpcomingPreOrdersError() {
    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    orderEmitter.onError(new DataMappingException());

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку полуения списка предзаказов.
   */
  @Test
  public void answerErrorOnPreOrdersListError() {
    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    orderEmitter.onNext(order2);
    ordersEmitter.onError(new DataMappingException());

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если пришедшего предзаказа больше нет в списке.
   */
  @Test
  public void answerOrderCancelledError() {
    // Дано:
    when(order2.withEtaToStartPoint(anyLong()))
        .thenReturn(order, order1, order, order3, order2, order);

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    orderEmitter.onNext(order2);
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order3, order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order1, order)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order3, order1, order2)));
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order2, order)));

    // Результат:
    testSubscriber.assertError(OrderCancelledException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertValues(order, order1);
  }

  /**
   * Должен завершить получение предстоящих предзаказов.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Не должен возвращать полученые ранее предстоящие предзаказы после ошибки их получения.
   */
  @Test
  public void answerNothingAfterUpcomingPreOrdersError() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    Flowable<Order> orders = useCase.getOrders();
    TestSubscriber<Order> testSubscriber = orders.test();
    orderEmitter.onNext(order1);
    orderEmitter.onNext(order);
    orderEmitter.onNext(order2);
    TestSubscriber<Order> testSubscriber0 = orders.test();
    orderEmitter.onError(new Exception());
    TestSubscriber<Order> testSubscriber1 = orders.test();

    // Результат:
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertValues(order2, order3, order1);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertError(Exception.class);
    testSubscriber0.assertValues(order1);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }

  /**
   * Не должен возвращать полученые ранее предстоящие предзаказы после ошибки получения списка
   * предзаказов.
   */
  @Test
  public void answerNothingAfterPreOrdersListError() {
    // Действие:
    Flowable<Order> orders = useCase.getOrders();
    TestSubscriber<Order> testSubscriber = orders.test();
    orderEmitter.onNext(order1);
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    orderEmitter.onNext(order);
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    orderEmitter.onNext(order2);
    ordersEmitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    TestSubscriber<Order> testSubscriber0 = orders.test();
    ordersEmitter.onError(new Exception());
    TestSubscriber<Order> testSubscriber1 = orders.test();

    // Результат:
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertValues(order2, order3, order1);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertError(Exception.class);
    testSubscriber0.assertValues(order1);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }

  /**
   * Не должен возвращать полученые ранее предстоящие предзаказы после завершения получения
   * предстоящих предзаказов.
   */
  @Test
  public void answerNothingAfterUpComingPreOrdersComplete() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    Flowable<Order> orders = useCase.getOrders().doOnNext(System.out::println);
    TestSubscriber<Order> testSubscriber = orders.test();
    orderEmitter.onNext(order1);
    orderEmitter.onNext(order);
    orderEmitter.onNext(order2);
    TestSubscriber<Order> testSubscriber0 = orders.test();
    orderEmitter.onComplete();
    TestSubscriber<Order> testSubscriber1 = orders.test();

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertValues(order2, order3, order1);
    testSubscriber.assertComplete();
    testSubscriber0.assertNoErrors();
    testSubscriber0.assertValues(order1);
    testSubscriber0.assertComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}