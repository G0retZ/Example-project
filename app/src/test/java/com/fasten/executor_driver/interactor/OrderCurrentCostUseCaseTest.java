package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.gateway.DataMappingException;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCurrentCostUseCaseTest {

  private OrderCurrentCostUseCase orderCurrentCostUseCase;
  @Mock
  private OrderGateway orderGateway;
  @Mock
  private OrderExcessCostGateway orderExcessCostGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(orderGateway.getOrders()).thenReturn(Flowable.never());
    when(orderExcessCostGateway.getOrderExcessCost()).thenReturn(Flowable.never());
    orderCurrentCostUseCase = new OrderCurrentCostUseCaseImpl(orderGateway,
        orderExcessCostGateway);
  }

  /* Проверяем работу с гейтвеем заказа */

  /**
   * Должен запросить у гейтвея получение выполняемого заказа.
   */
  @Test
  public void askOrderGatewayForOrders() {
    // Действие:
    orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    verify(orderGateway, only()).getOrders();
  }

  /* Проверяем работу с гейтвеем текущей цены заказа */

  /**
   * Не должен трогать гейтвея, пока не было заказа.
   */
  @Test
  public void doNotTouchCurrentCostGateway() {
    // Действие:
    orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    verifyZeroInteractions(orderExcessCostGateway);
  }

  /**
   * Должен сообщить гейтвею о начале погрузки.
   */
  @Test
  public void askCurrentCostGatewayForCostUpdates() {
    // Дано:
    when(orderGateway.getOrders()).thenReturn(Flowable.just(order));

    // Действие:
    orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    verify(orderExcessCostGateway, only()).getOrderExcessCost();
  }

  /* Проверяем ответы на запрос цены заказа */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(orderGateway.getOrders())
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Integer> test = orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingErrorInCurrentCost() {
    // Дано:
    when(orderGateway.getOrders())
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(101);
    when(orderExcessCostGateway.getOrderExcessCost())
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Integer> test = orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertValue(101);
    test.assertNotComplete();
  }

  /**
   * Должен ответить ценами только из заказов.
   */
  @Test
  public void answerWithOrdersCostsOnly() {
    // Дано:
    when(orderGateway.getOrders())
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(110);
    when(order2.getTotalCost()).thenReturn(12173);

    // Действие:
    TestSubscriber<Integer> test = orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    test.assertValues(110, 12173);
    test.assertNotComplete();
    test.assertNoErrors();
  }

  /**
   * Должен ответить ценами из заказов и обновлениями цен.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithOrdersAndUpdatedCosts() {
    // Дано:
    when(orderGateway.getOrders())
        .thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(100);
    when(order2.getTotalCost()).thenReturn(12173);
    when(orderExcessCostGateway.getOrderExcessCost()).thenReturn(
        Flowable.just(123, 145, 139, 198, 202),
        Flowable.just(8395, 8937, 17156, 9228)
    );

    // Действие:
    TestSubscriber<Integer> test = orderCurrentCostUseCase.getOrderCurrentCost().test();

    // Результат:
    test.assertValues(100, 123, 145, 139, 198, 202, 12173, 8395, 8937, 17156, 9228);
    test.assertComplete();
    test.assertNoErrors();
  }
}