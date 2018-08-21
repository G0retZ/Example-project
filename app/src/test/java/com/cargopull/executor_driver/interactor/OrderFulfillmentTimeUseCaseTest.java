package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderFulfillmentTimeUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderFulfillmentTimeUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderGateway gateway;
  @Mock
  private DataReceiver<String> loginReceiver;
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
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(gateway.getOrders(anyString())).thenReturn(Flowable.never());
    useCase = new OrderFulfillmentTimeUseCaseImpl(errorReporter, gateway, loginReceiver, timeUtils);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    useCase.getOrderElapsedTime().test();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение времени выполняемого заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getOrderElapsedTime().test();

    // Результат:
    verify(gateway).getOrders("1234567890");
    verify(gateway).getOrders("0987654321");
    verify(gateway).getOrders("123454321");
    verify(gateway).getOrders("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getOrders("1234567890")).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrderElapsedTime().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы на запрос времени заказа */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getOrders("1234567890")).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderElapsedTime().test();

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
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(gateway.getOrders("1234567890"))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(order.getStartTime()).thenReturn(12345000L);
    when(order2.getStartTime()).thenReturn(6789000L);
    when(timeUtils.currentTimeMillis()).thenReturn(12350000L, 6801000L);

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderElapsedTime().test();
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