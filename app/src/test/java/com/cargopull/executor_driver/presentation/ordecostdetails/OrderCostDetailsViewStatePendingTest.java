package com.cargopull.executor_driver.presentation.ordecostdetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsViewStatePendingTest {

  private OrderCostDetailsViewStatePending viewState;

  @Mock
  private OrderCostDetailsViewActions viewActions;

  @Mock
  private ViewState<OrderCostDetailsViewActions> parentViewState;
  @Mock
  private ViewState<OrderCostDetailsViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new OrderCostDetailsViewStatePending(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(true);
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderCostDetailsViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(true);
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderCostDetailsViewStatePending(parentViewState);
    assertEquals(viewState, viewState);
    assertEquals(viewState, new OrderCostDetailsViewStatePending(parentViewState));
    assertNotEquals(viewState, new OrderCostDetailsViewStatePending(parentViewState1));
    assertNotEquals(viewState, new OrderCostDetailsViewStatePending(null));
    assertNotEquals(viewState, "");
    assertNotEquals(viewState, null);
  }

  @Test
  public void testHashCode() {
    assertNotEquals(viewState.hashCode(), parentViewState1.hashCode());
    assertEquals(viewState.hashCode(), parentViewState.hashCode());
  }
}