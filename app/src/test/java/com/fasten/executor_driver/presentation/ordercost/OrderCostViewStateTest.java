package com.fasten.executor_driver.presentation.ordercost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostViewStateTest {

  private OrderCostViewState viewState;

  @Mock
  private OrderCostViewActions orderCostViewActions;

  @Before
  public void setUp() {
    viewState = new OrderCostViewState(12345);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderCostViewActions);

    // Результат:
    verify(orderCostViewActions, only()).setOrderCostText(12345);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderCostViewState(12345));
    assertNotEquals(viewState, new OrderCostViewState(54321));
  }
}