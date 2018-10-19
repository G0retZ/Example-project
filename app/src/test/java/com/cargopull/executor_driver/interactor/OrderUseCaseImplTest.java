package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderUseCaseImplTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderUseCaseImpl useCase;

  @Mock
  private CommonGateway<Order> gateway;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  private FlowableEmitter<Order> emitter;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new OrderUseCaseImpl(gateway);
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
    useCase.setOrderOfferDecisionMade();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.setOrderOfferDecisionMade();

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем ответы */

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(gateway.getData())
        .thenReturn(Flowable.just(order, order1, order2).concatWith(Flowable.never()));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
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
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerPreOrderExpiredError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.just(order, order1, order2)
        .concatWith(Flowable.error(new OrderOfferExpiredException(""))));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setOrderOfferDecisionMade();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(OrderOfferExpiredException.class);
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerOrderOfferDecisionError() {
    // Дано:
    when(gateway.getData())
        .thenReturn(Flowable.just(order, order1, order2).concatWith(Flowable.never()));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setOrderOfferDecisionMade();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(OrderOfferDecisionException.class);
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
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

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
    when(gateway.getData()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER)
    );

    // Действие:
    Flowable<Order> orders = useCase.getOrders();
    TestSubscriber<Order> testSubscriber = orders.test();
    emitter.onNext(order);
    emitter.onNext(order1);
    emitter.onNext(order2);
    TestSubscriber<Order> testSubscriber0 = orders.test();
    emitter.onError(new Exception());
    TestSubscriber<Order> testSubscriber1 = orders.test();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertValues(order2);
    testSubscriber0.assertError(Exception.class);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }

  /**
   * Не должен возвращать полученые ранее заказы после принятия решения.
   */
  @Test
  public void answerNothingAfterPreOrderExpiredError() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER)
    );

    // Действие:
    Flowable<Order> orders = useCase.getOrders();
    TestSubscriber<Order> testSubscriber = orders.test();
    emitter.onNext(order);
    emitter.onNext(order1);
    emitter.onNext(order2);
    TestSubscriber<Order> testSubscriber0 = orders.test();
    useCase.setOrderOfferDecisionMade();
    TestSubscriber<Order> testSubscriber1 = orders.test();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(OrderOfferDecisionException.class);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertValues(order2);
    testSubscriber0.assertError(OrderOfferDecisionException.class);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}