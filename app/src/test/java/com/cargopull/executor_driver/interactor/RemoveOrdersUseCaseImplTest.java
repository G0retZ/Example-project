package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.cargopull.executor_driver.entity.Order;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoveOrdersUseCaseImplTest {

  private OrdersUseCase removeOrdersUseCase;
  @Mock
  private OrdersUseCase ordersUseCase;
  @Mock
  private Order order;

  @Before
  public void setUp() {
    removeOrdersUseCase = new RemoveOrdersUseCaseImpl(ordersUseCase);
  }

  @Test
  public void shouldAskForOrdersSet() {
    // Action:
    removeOrdersUseCase.getOrdersSet();

      // Effect:
    verify(ordersUseCase, only()).getOrdersSet();
  }

  @Test
  public void shouldAskForRemoveOrderOnAdd() {
      // Action:
    removeOrdersUseCase.addOrder(order);

      // Effect:
    verify(ordersUseCase, only()).removeOrder(order);
  }

  @Test
  public void shouldAskForRemoveOrderOnRemove() {
      // Action:
    removeOrdersUseCase.removeOrder(order);

      // Effect:
    verify(ordersUseCase, only()).removeOrder(order);
  }
}