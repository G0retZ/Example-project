package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderFulfillmentTimeUseCaseTest {

  private OrderFulfillmentTimeUseCase orderFulfillmentTimeUseCase;

  @Mock
  private OrderGateway orderGateway;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private Order order;
  @Mock
  private Order order2;
  private TestScheduler testScheduler;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    when(orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT)).thenReturn(Flowable.never());
    orderFulfillmentTimeUseCase = new OrderFulfillmentTimeUseCaseImpl(orderGateway, timeUtils);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение времени выполняемого заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Действие:
    orderFulfillmentTimeUseCase.getOrderElapsedTime().test();

    // Результат:
    verify(orderGateway, only()).getOrders(ExecutorState.ORDER_FULFILLMENT);
  }

  /* Проверяем ответы на запрос времени заказа */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT))
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Long> test = orderFulfillmentTimeUseCase.getOrderElapsedTime().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить обновлениями времени.
   */
  @Test
  public void answerWithTimeUpdates() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(order.getOrderStartTime()).thenReturn(12345000L);
    when(order2.getOrderStartTime()).thenReturn(6789000L);
    when(timeUtils.currentTimeMillis()).thenReturn(12350000L, 6801000L);

    // Действие:
    TestSubscriber<Long> test = orderFulfillmentTimeUseCase.getOrderElapsedTime().test();
    publishSubject.onNext(order);
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    publishSubject.onNext(order2);
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(8, TimeUnit.SECONDS);

    // Результат:
    test.assertValues(5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 12L, 13L, 14L, 15L, 16L,
        17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L);
    test.assertNotComplete();
    test.assertNoErrors();
  }
}