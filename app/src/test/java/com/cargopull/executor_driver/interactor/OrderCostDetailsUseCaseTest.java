package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.OrderCostDetails;
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
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderCostDetailsUseCaseImpl useCase;

  @Mock
  private CommonGateway<OrderCostDetails> gateway;
  @Mock
  private OrderCostDetails orderCostDetails;
  @Mock
  private OrderCostDetails orderCostDetails1;
  private FlowableEmitter<OrderCostDetails> emitter;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new OrderCostDetailsUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение детального расчета заказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Action:
    useCase.getOrderCostDetails().test().isDisposed();
    useCase.getOrderCostDetails().test().isDisposed();
    useCase.getOrderCostDetails().test().isDisposed();
    useCase.getOrderCostDetails().test().isDisposed();

    // Effect:
    verify(gateway, only()).getData();
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить детальными расчетами заказа.
   */
  @Test
  public void answerWithOrderCostDetails() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.just(orderCostDetails,
        orderCostDetails1));

    // Action:
    TestSubscriber<OrderCostDetails> test = useCase.getOrderCostDetails().test();

    // Effect:
    test.assertValues(orderCostDetails, orderCostDetails1);
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить детальными расчетами заказа и из обновлений в том числе.
   */
  @Test
  public void valueUnchangedForRead() {
    // Given:
    when(gateway.getData()).thenReturn(
        Flowable.just(orderCostDetails, orderCostDetails1).concatWith(Flowable.never())
    );
    TestSubscriber<OrderCostDetails> testObserver = useCase.getOrderCostDetails().test();

    // Action:
    useCase.updateWith(orderCostDetails1);
    useCase.updateWith(orderCostDetails);

    // Effect:
    testObserver
        .assertValues(orderCostDetails, orderCostDetails1, orderCostDetails1, orderCostDetails);
  }

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.error(new DataMappingException()));

    // Action:
    TestSubscriber<OrderCostDetails> test = useCase.getOrderCostDetails().test();

    // Effect:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен завершить получение заказов.
   */
  @Test
  public void answerComplete() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Action:
    TestSubscriber<OrderCostDetails> testSubscriber = useCase.getOrderCostDetails().test();
    useCase.updateWith(orderCostDetails);

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
    when(gateway.getData()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER)
    );

    // Action:
    Flowable<OrderCostDetails> orders = useCase.getOrderCostDetails();
    TestSubscriber<OrderCostDetails> testSubscriber = orders.test();
    emitter.onNext(orderCostDetails);
    emitter.onNext(orderCostDetails1);
    useCase.updateWith(orderCostDetails);
    TestSubscriber<OrderCostDetails> testSubscriber0 = orders.test();
    emitter.onError(new Exception());
    TestSubscriber<OrderCostDetails> testSubscriber1 = orders.test();

    // Effect:
    testSubscriber.assertValues(orderCostDetails, orderCostDetails1, orderCostDetails);
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertValues(orderCostDetails);
    testSubscriber0.assertError(Exception.class);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}