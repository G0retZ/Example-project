package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CommonGateway<Order> gateway;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new OrderUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа только раз.
   */
  @Test
  public void askGatewayForOrdersOnlyOnce() {
    // Действие:
    useCase.getOrders().test();
    useCase.getOrders().test();
    useCase.setOrderOfferDecisionMade();
    useCase.getOrders().test();
    useCase.getOrders().test();
    useCase.setOrderOfferDecisionMade();

    // Результат:
    verify(gateway, only()).getData();
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
    useCase.getOrders().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку потери актуальности заказа.
   */
  @Test
  public void reportPreOrderExpiredError() {
    // Действие:
    useCase.getOrders().test();
    useCase.setOrderOfferDecisionMade();

    // Результат:
    verify(errorReporter, only()).reportError(any(OrderOfferDecisionException.class));
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
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<Order>() {
      private boolean run;

      @Override
      public void subscribe(FlowableEmitter<Order> emitter) {
        if (!run) {
          emitter.onNext(order);
          emitter.onNext(order1);
          emitter.onNext(order2);
          emitter.onError(new Exception());
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    TestSubscriber<Order> testSubscriber1 = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertNotComplete();
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
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<Order>() {
      private boolean run;

      @Override
      public void subscribe(FlowableEmitter<Order> emitter) {
        if (!run) {
          emitter.onNext(order);
          emitter.onNext(order1);
          emitter.onNext(order2);
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setOrderOfferDecisionMade();
    TestSubscriber<Order> testSubscriber1 = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(OrderOfferDecisionException.class);
    testSubscriber.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}