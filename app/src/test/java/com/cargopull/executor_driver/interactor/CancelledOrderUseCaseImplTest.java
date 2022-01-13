package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class CancelledOrderUseCaseImplTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private CancelledOrderUseCaseImpl useCase;

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
    useCase = new CancelledOrderUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение отмененных заказа только раз.
   */
  @Test
  public void askGatewayForOrdersOnlyOnce() {
    // Action:
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();

    // Effect:
    verify(gateway, only()).getData();
  }

  /* Проверяем ответы */

  /**
   * Должен ответить отмененными заказами.
   */
  @Test
  public void answerWithOrders() {
    // Given:
    when(gateway.getData())
        .thenReturn(Flowable.just(order, order1, order2).concatWith(Flowable.never()));

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerError() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение отмененных заказов.
   */
  @Test
  public void answerComplete() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

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
    // Given:
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<Order>() {
      private boolean run;

      @Override
      public void subscribe(@NonNull FlowableEmitter<Order> emitter) {
        if (!run) {
          emitter.onNext(order);
          emitter.onNext(order1);
          emitter.onNext(order2);
          emitter.onError(new Exception());
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    TestSubscriber<Order> testSubscriber1 = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertValues(order, order1, order2);
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}