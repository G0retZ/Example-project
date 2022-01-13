package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.TimeUtils;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class OrderFulfillmentTimeUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderFulfillmentTimeUseCase useCase;

  @Mock
  private OrderUseCase orderUseCase;
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
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    when(orderUseCase.getOrders()).thenReturn(Flowable.never());
    useCase = new OrderFulfillmentTimeUseCaseImpl(orderUseCase, timeUtils);
  }

  /* Проверяем работу с юзкейсом заказа */

  /**
   * Должен запросить у юзкейса получение времени выполняемого заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Action:
    useCase.getOrderElapsedTime().test().isDisposed();
    useCase.getOrderElapsedTime().test().isDisposed();
    useCase.getOrderElapsedTime().test().isDisposed();
    useCase.getOrderElapsedTime().test().isDisposed();

    // Effect:
    verify(orderUseCase, times(4)).getOrders();
    verifyNoMoreInteractions(orderUseCase);
  }

  /* Проверяем ответы на запрос времени заказа */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Given:
    when(orderUseCase.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Action:
    TestSubscriber<Long> test = useCase.getOrderElapsedTime().test();

    // Effect:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить обновлениями времени.
   */
  @Test
  public void answerWithTimeUpdates() {
    // Given:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(order.getStartTime()).thenReturn(12345000L);
    when(order2.getStartTime()).thenReturn(6789000L);
    when(timeUtils.currentTimeMillis()).thenReturn(12350000L, 6801000L);

    // Action:
    TestSubscriber<Long> test = useCase.getOrderElapsedTime().test();
    publishSubject.onNext(order);
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    publishSubject.onNext(order2);
    testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    testScheduler.advanceTimeBy(8, TimeUnit.SECONDS);

    // Effect:
    test.assertValues(5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 12L, 13L, 14L, 15L, 16L,
        17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L);
    test.assertNotComplete();
    test.assertNoErrors();
  }
}