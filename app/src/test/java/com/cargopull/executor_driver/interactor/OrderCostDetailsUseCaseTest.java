package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private OrderCostDetailsUseCase useCase;

  @Mock
  private CommonGateway<OrderCostDetails> gateway;
  @Mock
  private OrderCostDetails orderCostDetails;
  @Mock
  private OrderCostDetails orderCostDetails1;

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
    // Действие:
    useCase.getOrderCostDetails().test();

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<OrderCostDetails> test = useCase.getOrderCostDetails().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить детальными расчетами заказа.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.just(orderCostDetails,
        orderCostDetails1));

    // Действие:
    TestSubscriber<OrderCostDetails> test = useCase.getOrderCostDetails().test();

    // Результат:
    test.assertValues(orderCostDetails, orderCostDetails1);
    test.assertComplete();
    test.assertNoErrors();
  }
}