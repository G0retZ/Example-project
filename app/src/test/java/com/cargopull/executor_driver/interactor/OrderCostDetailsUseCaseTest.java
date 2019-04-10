package com.cargopull.executor_driver.interactor;

import com.cargopull.executor_driver.entity.OrderCostDetails;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsUseCaseTest {

  private OrderCostDetailsUseCase useCase;

  @Mock
  private OrderCostDetails orderCostDetails;
  @Mock
  private OrderCostDetails orderCostDetails1;

  @Before
  public void setUp() {
    useCase = new OrderCostDetailsUseCase();
  }


  /**
   * Должен получить значение без изменений.
   */
  @Test
  public void valueUnchangedForRead() {
    // Дано:
    TestObserver<OrderCostDetails> testObserver = useCase.get().test();

    // Действие:
    useCase.updateWith(orderCostDetails);
    useCase.updateWith(orderCostDetails1);

    // Результат:
    testObserver.assertValues(orderCostDetails, orderCostDetails1);
  }
}