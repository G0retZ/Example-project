package com.fasten.executor_driver.presentation.waitingforclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WaitingForClientViewStateTest {

  private WaitingForClientViewState viewState;

  @Mock
  private WaitingForClientViewActions waitingForClientViewActions;

  @Mock
  private OrderItem orderItem;
  @Mock
  private OrderItem orderItem2;

  @Before
  public void setUp() {
    viewState = new WaitingForClientViewState(orderItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(orderItem.getOrderComment()).thenReturn("comm");
    when(orderItem.getEstimatedPrice()).thenReturn("1000");
    when(orderItem.getOrderOptionsRequired()).thenReturn("1,2,3");

    // Действие:
    viewState.apply(waitingForClientViewActions);

    // Результат:
    verify(waitingForClientViewActions).showEstimatedPrice("1000");
    verify(waitingForClientViewActions).showOrderOptionsRequirements("1,2,3");
    verify(waitingForClientViewActions).showComment("comm");
    verifyNoMoreInteractions(waitingForClientViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new WaitingForClientViewState(null);

    // Действие:
    viewState.apply(waitingForClientViewActions);

    // Результат:
    verifyZeroInteractions(waitingForClientViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new WaitingForClientViewState(orderItem));
    assertNotEquals(viewState, new WaitingForClientViewState(orderItem2));
    assertNotEquals(viewState, new WaitingForClientViewState(null));
  }
}