package com.cargopull.executor_driver.presentation.ordercost;

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
  private OrderCostViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new OrderCostViewState(12345);
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).setOrderCostText(12345);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderCostViewState(12345));
    assertNotEquals(viewState, new OrderCostViewState(54321));
  }
}