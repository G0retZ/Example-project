package com.fasten.executor_driver.presentation.ordercost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostViewStateErrorTest {

  private OrderCostViewStateError viewState;

  @Mock
  private OrderCostViewActions orderCostViewActions;

  @Before
  public void setUp() {
    viewState = new OrderCostViewStateError(12345);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderCostViewActions);

    // Результат:
    verify(orderCostViewActions).setOrderCostText(12345);
    verify(orderCostViewActions).showOrderCostNetworkErrorMessage(true);
    verifyNoMoreInteractions(orderCostViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderCostViewStateError(12345));
    assertNotEquals(viewState, new OrderCostViewStateError(54321));
  }
}