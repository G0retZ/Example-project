package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
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
public class OrdersUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrdersUseCase useCase;

  @Mock
  private CommonGateway<Set<Order>> gateway;
  @Mock
  private OrderUseCase changedOrdersUseCase;
  @Mock
  private OrderUseCase cancelledOrdersUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;
  private FlowableEmitter<Set<Order>> emitter;
  private FlowableEmitter<Order> changedEmitter;
  private FlowableEmitter<Order> cancelledEmitter;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(
        Flowable.create(e -> emitter = e, BackpressureStrategy.BUFFER)
    );
    when(changedOrdersUseCase.getOrders()).thenReturn(
        Flowable.create(e -> changedEmitter = e, BackpressureStrategy.BUFFER)
    );
    when(cancelledOrdersUseCase.getOrders()).thenReturn(
        Flowable.create(e -> cancelledEmitter = e, BackpressureStrategy.BUFFER)
    );
    useCase = new OrdersUseCaseImpl(gateway, changedOrdersUseCase, cancelledOrdersUseCase);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение запланированных предзаказов только раз.
   */
  @Test
  public void askGatewayForOrdersOnlyOnce() {
    // Action:
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.removeOrder(order3);

    // Effect:
    verify(gateway, only()).getData();
  }

  /* Проверяем работу с юзкейсом измененного предзаказа */

  /**
   * Не должен запрашивать у юзкейса получение измененных предзаказов если еще не было списков
   * заказов.
   */
  @Test
  public void doNotAskUseCaseForChangedOrders() {
    // Action:
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.removeOrder(order3);

    // Effect:
    verifyNoInteractions(changedOrdersUseCase);
  }

  /**
   * Должен запросить у юзкейса получение измененных предзаказов только раз.
   */
  @Test
  public void askUseCaseForChangedOrdersForEveryAdd() {
    // Action:
    useCase.getOrdersSet().test().isDisposed();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.getOrdersSet().test().isDisposed();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.removeOrder(order3);

    // Effect:
    verify(changedOrdersUseCase, only()).getOrders();
  }

  /* Проверяем работу с юзкейсом отмененного предзаказа */

  /**
   * Не должен запрашивать у юзкейса получение отмененных предзаказов если еще не было списков
   * заказов.
   */
  @Test
  public void doNotAskUseCaseForCancelledOrders() {
    // Action:
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.removeOrder(order3);

    // Effect:
    verifyNoInteractions(cancelledOrdersUseCase);
  }

  /**
   * Должен запросить у юзкейса получение отмененных предзаказов только раз.
   */
  @Test
  public void askUseCaseForCancelledOrdersForEveryAdd() {
    // Action:
    useCase.getOrdersSet().test().isDisposed();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.getOrdersSet().test().isDisposed();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test().isDisposed();
    useCase.getOrdersSet().test().isDisposed();
    useCase.removeOrder(order3);

    // Effect:
    verify(cancelledOrdersUseCase, times(2)).getOrders();
    verifyNoMoreInteractions(cancelledOrdersUseCase);
  }

  /* Проверяем ответы */

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOrders() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));

    // Effect:
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerError() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onError(new DataMappingException());

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть список, затем его обновление без удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnUnSchedule() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.removeOrder(order2);
    useCase.removeOrder(order);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order1, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление с измененным заказом.
   */
  @Test
  public void answerWithUpdatedListOnChangedOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    changedEmitter.onNext(order2);
    changedEmitter.onNext(order);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление без отмененного заказа.
   */
  @Test
  public void answerWithUpdatedListOnCancelledOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    cancelledEmitter.onNext(order2);
    cancelledEmitter.onNext(order);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order1, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление с измененным заказом и без удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnUnScheduleAndChangedOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    changedEmitter.onNext(order2);
    useCase.removeOrder(order);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order1, order2, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление без отмененного заказа и без удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnUnScheduleAndCancelledOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    cancelledEmitter.onNext(order2);
    useCase.removeOrder(order);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order1, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление с добавленным элементом.
   */
  @Test
  public void answerWithUpdatedListOnSchedule() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order3)));
    useCase.addOrder(order2);
    useCase.addOrder(order1);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order3, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order3, order2, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его без удаленного элемента, затем его же с тем же добавленным
   * элементом.
   */
  @Test
  public void answerWithUpdatedListOnUnScheduleThenSchedule() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.removeOrder(order2);
    useCase.addOrder(order2);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем с добавленным элементом, затем его же с без того же удаленного
   * элемента.
   */
  @Test
  public void answerWithUpdatedListOnScheduleThenUnSchedule() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order3)));
    useCase.addOrder(order2);
    useCase.removeOrder(order2);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order3, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть новый список, после добавлений, удалений, отмен и изменений заказов.
   */
  @Test
  public void answerWithNewListAfterSchedulesAndUnSchedules() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order3)));
    useCase.addOrder(order2);
    useCase.removeOrder(order2);
    useCase.addOrder(order1);
    useCase.addOrder(order2);
    changedEmitter.onNext(order2);
    cancelledEmitter.onNext(order1);
    useCase.removeOrder(order3);
    useCase.addOrder(order1);
    useCase.addOrder(order3);
    changedEmitter.onNext(order3);
    cancelledEmitter.onNext(order1);
    emitter.onNext(new HashSet<>(Arrays.asList(order1, order2)));

    // Effect:
    testSubscriber.assertValueCount(13);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order3, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(3, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(4, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(5, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(6, new HashSet<>(Arrays.asList(order, order2, order3)));
    testSubscriber.assertValueAt(7, new HashSet<>(Arrays.asList(order, order2)));
    testSubscriber.assertValueAt(8, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertValueAt(9, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(10, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(11, new HashSet<>(Arrays.asList(order, order2, order3)));
    testSubscriber.assertValueAt(12, new HashSet<>(Arrays.asList(order1, order2)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошено удаленние отсутствующего элемента.
   */
  @Test
  public void answerWithSameListOnUnSchedule() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    useCase.removeOrder(order2);
    useCase.removeOrder(order3);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если отменен отсутствующий заказ .
   */
  @Test
  public void answerWithSameListOnCancelledOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    cancelledEmitter.onNext(order2);
    cancelledEmitter.onNext(order3);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошены удаленние и отменена отсутствующих заказов.
   */
  @Test
  public void answerWithSameListOnUnScheduleAndCancelledOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    cancelledEmitter.onNext(order2);
    useCase.removeOrder(order3);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошено добавление присутствующего элемента.
   */
  @Test
  public void answerWithSameListOnSchedule() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    useCase.addOrder(order);
    useCase.addOrder(order1);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен дополнить список, если изменен отсутствующий заказ .
   */
  @Test
  public void answerWithAddedListOnChangedOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    changedEmitter.onNext(order2);
    changedEmitter.onNext(order3);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен дополнить список только измененным заказом, если запрошены добавление и изменение
   * отсутствующего заказа.
   */
  @Test
  public void answerWithAddedListOnScheduleAndChangedOrder() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    changedEmitter.onNext(order2);
    useCase.addOrder(order1);

    // Effect:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен завершить получение заказов.
   */
  @Test
  public void answerComplete() {
    // Action:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onComplete();

    // Effect:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Не должен возвращать полученые ранее заказы после ошибки.
   */
  @Test
  public void answerNothingAfterError() {
    // Action:
    Flowable<Set<Order>> ordersSet = useCase.getOrdersSet();
    TestSubscriber<Set<Order>> testSubscriber = ordersSet.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    emitter.onError(new Exception());
    TestSubscriber<Set<Order>> testSubscriber1 = ordersSet.test();

    // Effect:
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }

  /**
   * Не должен возвращать полученые ранее заказы после завершения.
   */
  @Test
  public void answerNothingAfterPreOrderExpiredError() {
    // Action:
    Flowable<Set<Order>> ordersSet = useCase.getOrdersSet();
    TestSubscriber<Set<Order>> testSubscriber = ordersSet.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    emitter.onComplete();
    TestSubscriber<Set<Order>> testSubscriber1 = ordersSet.test();

    // Effect:
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}