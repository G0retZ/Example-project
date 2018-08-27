package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCurrentCostUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderCurrentCostUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private OrderCurrentCostGateway orderCurrentCostGateway;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(orderUseCase.getOrders()).thenReturn(Flowable.never());
    when(orderCurrentCostGateway.getOrderCurrentCost()).thenReturn(Flowable.never());
    useCase = new OrderCurrentCostUseCaseImpl(errorReporter, orderUseCase, orderCurrentCostGateway);
  }

  /* Проверяем работу с юзкейсом заказа */

  /**
   * Должен запросить у юзкейса получение выполняемого заказа.
   */
  @Test
  public void askOrderGatewayForOrders() {
    // Действие:
    useCase.getOrderCurrentCost().test();
    useCase.getOrderCurrentCost().test();
    useCase.getOrderCurrentCost().test();
    useCase.getOrderCurrentCost().test();

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
    useCase.getOrderCurrentCost().test();

    // Результат:
    verifyZeroInteractions(orderCurrentCostGateway);
  }

  /**
   * Должен запросить у гейтвея информацию о текущей цене.
   */
  @Test
  public void askCurrentCostGatewayForCostUpdates() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(orderCurrentCostGateway, only()).getOrderCurrentCost();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingErrorInCurrentCost() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.just(order, order2));
    when(order.getTotalCost()).thenReturn(101L);
    when(orderCurrentCostGateway.getOrderCurrentCost())
        .thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrderCurrentCost().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
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
    when(orderCurrentCostGateway.getOrderCurrentCost())
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
    when(orderCurrentCostGateway.getOrderCurrentCost()).thenReturn(
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