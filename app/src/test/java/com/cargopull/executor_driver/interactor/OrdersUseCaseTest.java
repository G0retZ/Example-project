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
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
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
  private OrderUseCase cancelledOrdersUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    when(cancelledOrdersUseCase.getOrders()).thenReturn(Flowable.never());
    useCase = new OrdersUseCaseImpl(errorReporter, gateway, cancelledOrdersUseCase);
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

  /* Проверяем работу с юзкейсом отмененного предзаказа */

  /**
   * Должен запросить у юзкейса получение отмененных предзаказов только раз.
   */
  @Test
  public void askUseCaseForCancelledOrdersOnlyOnce() {
    // Действие:
    useCase.getOrdersSet().test();
    useCase.getOrdersSet().test();
    useCase.addOrder(order1);
    useCase.getOrdersSet().test();
    useCase.getOrdersSet().test();
    useCase.removeOrder(order3);

    // Результат:
    verify(cancelledOrdersUseCase, only()).getOrders();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getOrdersSet().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportErrorForCancelledOrder() {
    // Дано:
    when(cancelledOrdersUseCase.getOrders()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getOrdersSet().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();

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
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();

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
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
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
   * Должен вернуть список, затем его обновление без отмененного заказа.
   */
  @Test
  public void answerWithUpdatedListOnCancelledOrder() {
    // Дано:
    PublishSubject<Order> orderPublishSubject = PublishSubject.create();
    when(cancelledOrdersUseCase.getOrders())
        .thenReturn(orderPublishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    orderPublishSubject.onNext(order2);
    orderPublishSubject.onNext(order);

    // Результат:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1, order2, order3)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1, order3)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order1, order3)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление без отмененного заказа и без удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnUnScheduleAndCancelledOrder() {
    // Дано:
    PublishSubject<Order> orderPublishSubject = PublishSubject.create();
    when(cancelledOrdersUseCase.getOrders())
        .thenReturn(orderPublishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    orderPublishSubject.onNext(order2);
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
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
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
   * Должен вернуть тот же список, если запрошено удаленние отсутствующего элемента.
   */
  @Test
  public void answerWithSameListOnUnSchedule() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
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
   * Должен вернуть тот же список, если отменен отсутствующий заказ .
   */
  @Test
  public void answerWithSameListOnCancelledOrder() {
    // Дано:
    PublishSubject<Order> orderPublishSubject = PublishSubject.create();
    when(cancelledOrdersUseCase.getOrders())
        .thenReturn(orderPublishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    orderPublishSubject.onNext(order2);
    orderPublishSubject.onNext(order3);

    // Результат:
    testSubscriber.assertValueCount(3);
    testSubscriber.assertValueAt(0, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(1, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertValueAt(2, new HashSet<>(Arrays.asList(order, order1)));
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошено удаленние и отменены отсутствующию заказы.
   */
  @Test
  public void answerWithSameListOnUnScheduleAndCancelledOrder() {
    // Дано:
    PublishSubject<Order> orderPublishSubject = PublishSubject.create();
    when(cancelledOrdersUseCase.getOrders())
        .thenReturn(orderPublishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    orderPublishSubject.onNext(order2);
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
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
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
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();

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
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<Set<Order>>() {
      private boolean run;

      @Override
      public void subscribe(FlowableEmitter<Set<Order>> emitter) {
        if (!run) {
          emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
          emitter.onError(new Exception());
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    TestSubscriber<Set<Order>> testSubscriber1 = useCase.getOrdersSet().test();

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
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<Set<Order>>() {
      private boolean run;

      @Override
      public void subscribe(FlowableEmitter<Set<Order>> emitter) {
        if (!run) {
          emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
          emitter.onComplete();
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Действие:
    TestSubscriber<Set<Order>> testSubscriber = useCase.getOrdersSet().test();
    TestSubscriber<Set<Order>> testSubscriber1 = useCase.getOrdersSet().test();

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