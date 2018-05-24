package com.fasten.executor_driver.presentation.ordertime;

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
public class OrderTimeViewStateTest {

  private OrderTimeViewState viewState;

  @Mock
  private OrderTimeViewActions orderTimeViewActions;

  @Before
  public void setUp() {
    viewState = new OrderTimeViewState(12345);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderTimeViewActions);

    // Результат:
    verify(orderTimeViewActions, only()).setOrderTimeText(12345);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderTimeViewState(12345));
    assertNotEquals(viewState, new OrderTimeViewState(54321));
  }
}