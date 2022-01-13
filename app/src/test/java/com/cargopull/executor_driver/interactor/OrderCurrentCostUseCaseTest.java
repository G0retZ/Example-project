package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class OrderCurrentCostUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderCurrentCostUseCase useCase;

  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private CommonGateway<Long> orderCurrentCostGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(orderUseCase.getOrders()).thenReturn(Flowable.never());
    when(orderCurrentCostGateway.getData()).thenReturn(Flowable.never());
    useCase = new OrderCurrentCostUseCaseImpl(orderUseCase, orderCurrentCostGateway);
  }

  /* Проверяем работу с юзкейсом заказа */

  /**
   * Должен запросить у юзкейса получение выполняемого заказа.
   */
  @Test
  public void askOrderGatewayForOrders() {
    // Действие:
    useCase.getOrderCurrentCost().test().isDisposed();
    useCase.getOrderCurrentCost().test().isDisposed();
    useCase.getOrderCurrentCost().test().isDisposed();
    useCase.getOrderCurrentCost().test().isDisposed();

    // Результат:
    verify(orderUseCase, times(4)).getOrders();
    verifyNoMoreInteractions(orderUseCase);
  }

  /* Проверяем работу с гейтвеем текущей цены заказа */

  /**
   * Не должен трогать гейтвея, пока не было заказа.
   */
  @Test
  public void doNotTouchCurrentCostGateway() {
    // Действие:
    useCase.getOrderCurrentCost().test().isDisposed();

    // Результат:
    verifyNoInteractions(orderCurrentCostGateway);
  }

  /**
   * Должен запросить у гейтвея информацию о текущей цене.
   */
  @Test
  public void askCurrentCostGatewayForCostUpdates() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order));

    // Действие:
    useCase.getOrderCurrentCost().test().isDisposed();

    // Результат:
    verify(orderCurrentCostGateway, only()).getData();
  }

  /* Проверяем ответы на запрос цены заказа */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

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
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(101L);
    when(orderCurrentCostGateway.getData())
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertValue(101L);
    test.assertNotComplete();
  }

  /**
   * Должен ответить ценами только из заказов.
   */
  @Test
  public void answerWithOrdersCostsOnly() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(110L);
    when(order2.getTotalCost()).thenReturn(12173L);

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertValues(110L, 12173L);
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
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(100L);
    when(order2.getTotalCost()).thenReturn(12173L);
    when(orderCurrentCostGateway.getData()).thenReturn(
        Flowable.just(123L, 145L, 139L, 198L, 202L),
        Flowable.just(8395L, 8937L, 17156L, 9228L)
    );

    // Действие:
    TestSubscriber<Long> test = useCase.getOrderCurrentCost().test();

    // Результат:
    test.assertValues(100L, 123L, 145L, 139L, 198L, 202L, 12173L, 8395L, 8937L, 17156L, 9228L);
    test.assertComplete();
    test.assertNoErrors();
  }
}