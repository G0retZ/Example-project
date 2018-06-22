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
public class OrderCostViewStateServerDataErrorTest {

  private OrderCostViewStateServerDataError viewState;

  @Mock
  private OrderCostViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new OrderCostViewStateServerDataError(12345);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setOrderCostText(12345);
    verify(viewActions).showOrderCostServerDataError();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderCostViewStateServerDataError(12345));
    assertNotEquals(viewState, new OrderCostViewStateServerDataError(54321));
  }
}