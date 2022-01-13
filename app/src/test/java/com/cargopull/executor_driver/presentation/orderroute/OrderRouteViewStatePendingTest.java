package com.cargopull.executor_driver.presentation.orderroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteViewStatePendingTest {

  private OrderRouteViewStatePending viewState;

  @Mock
  private OrderRouteViewActions viewActions;
  @Mock
  private ViewState<OrderRouteViewActions> parentViewState;
  @Mock
  private ViewState<OrderRouteViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Given:
    viewState = new OrderRouteViewStatePending(parentViewState);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).showOrderRoutePending(true);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testActionsWithNull() {
      // Given:
    viewState = new OrderRouteViewStatePending(null);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).showOrderRoutePending(true);
  }

  @Test
  public void testEquals() {
    viewState = new OrderRouteViewStatePending(parentViewState);
    assertEquals(viewState, new OrderRouteViewStatePending(parentViewState));
    assertNotEquals(viewState, new OrderRouteViewStatePending(parentViewState1));
    assertNotEquals(viewState, new OrderRouteViewStatePending(null));
  }
}