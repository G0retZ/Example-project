package com.fasten.executor_driver.presentation.ordertime;

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
public class OrderTimeViewStateServerDataErrorTest {

  private OrderTimeViewStateServerDataError viewState;

  @Mock
  private OrderTimeViewActions orderTimeViewActions;

  @Before
  public void setUp() {
    viewState = new OrderTimeViewStateServerDataError(12345);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderTimeViewActions);

    // Результат:
    verify(orderTimeViewActions).setOrderTimeText(12345);
    verify(orderTimeViewActions).showOrderTimeServerDataError();
    verifyNoMoreInteractions(orderTimeViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderTimeViewStateServerDataError(12345));
    assertNotEquals(viewState, new OrderTimeViewStateServerDataError(54321));
  }
}