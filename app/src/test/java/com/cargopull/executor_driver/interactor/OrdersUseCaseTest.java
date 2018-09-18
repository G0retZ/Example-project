package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrdersUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrdersUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CommonGateway<Set<Order>> gateway;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;
  private FlowableEmitter<Set<Order>> emitter;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(
        Flowable.create(e -> emitter = e, BackpressureStrategy.BUFFER)
    );
    useCase = new OrdersUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение запланированных предзаказов только раз.
   */
  @Test
  public void askGatewayForOrdersOnlyOnce() {
    // Действие:
    useCase.getOrdersSet().test();
    useCase.getOrdersSet().test();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test();
    useCase.getOrdersSet().test();
    useCase.removeOrder(order3);

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    useCase.getOrdersSet().test();
    emitter.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOrders() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));

    // Результат:
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
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onError(new DataMappingException());

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть список, затем его обновление без удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnUnSchedule() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.removeOrder(order2);
    useCase.removeOrder(order);

    // Результат:
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
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order3)));
    useCase.addOrder(order2);
    useCase.addOrder(order1);

    // Результат:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order3, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order3, order2, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его без удаленного элемента, затем его же с тем же добавленным элементом.
   */
  @Test
  public void answerWithUpdatedListOnUnScheduleThenSchedule() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.removeOrder(order2);
    useCase.addOrder(order2);

    // Результат:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем с добавленным элементом, затем его же с без того же удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnScheduleThenUnSchedule() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order3)));
    useCase.addOrder(order2);
    useCase.removeOrder(order2);

    // Результат:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order3, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть новый список, после добавлений и удаления элементов.
   */
  @Test
  public void answerWithNewListAfterSchedulesAndUnSchedules() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order3)));
    useCase.addOrder(order2);
    useCase.removeOrder(order2);
    useCase.addOrder(order1);
    useCase.addOrder(order2);
    useCase.removeOrder(order1);
    useCase.removeOrder(order3);
    useCase.addOrder(order1);
    useCase.addOrder(order3);
    emitter.onNext(new HashSet<>(Arrays.asList(order1, order2)));

    // Результат:
    testSubscriber.assertValueCount(10);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order3, order2)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order3)));
    testSubscriber.assertValueAt(3, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(4, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(5, new HashSet<>(Arrays.asList(order, order2, order3)));
    testSubscriber.assertValueAt(6, new HashSet<>(Arrays.asList(order, order2)));
    testSubscriber.assertValueAt(7, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertValueAt(8, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(9, new HashSet<>(Arrays.asList(order1, order2)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошено удаленние отсутствующего элемента.
   */
  @Test
  public void answerWithSameListOnUnSchedule() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1)));
    useCase.removeOrder(order2);
    useCase.removeOrder(order3);

    // Результат:
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
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    useCase.addOrder(order2);
    useCase.addOrder(order3);

    // Результат:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен завершить получение заказов.
   */
  @Test
  public void answerComplete() {
    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    emitter.onComplete();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Не должен возвращать полученые ранее заказы после ошибки.
   */
  @Test
  public void answerNothingAfterError() {
    // Действие:
    Flowable<Set<Order>> ordersSet = useCase.getOrdersSet();
    TestSubscriber<Set<Order>> testSubscriber = ordersSet.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    emitter.onError(new Exception());
    TestSubscriber<Set<Order>> testSubscriber1 = ordersSet.test();

    // Результат:
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
    // Действие:
    Flowable<Set<Order>> ordersSet = useCase.getOrdersSet();
    TestSubscriber<Set<Order>> testSubscriber = ordersSet.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    emitter.onComplete();
    TestSubscriber<Set<Order>> testSubscriber1 = ordersSet.test();

    // Результат:
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2)));
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}