package com.fasten.executor_driver.presentation.orderroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteViewStateErrorTest {

  private OrderRouteViewStateError viewState;

  @Mock
  private OrderRouteViewActions nextRoutePointViewActions;
  @Mock
  private ViewState<OrderRouteViewActions> parentViewState;
  @Mock
  private ViewState<OrderRouteViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new OrderRouteViewStateError(parentViewState);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions, only()).showOrderRouteErrorMessage(true);
    verify(parentViewState, only()).apply(nextRoutePointViewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new OrderRouteViewStateError(null);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions, only()).showOrderRouteErrorMessage(true);
  }

  @Test
  public void testEquals() {
    viewState = new OrderRouteViewStateError(parentViewState);
    assertEquals(viewState, new OrderRouteViewStateError(parentViewState));
    assertNotEquals(viewState, new OrderRouteViewStateError(parentViewState1));
    assertNotEquals(viewState, new OrderRouteViewStateError(null));
  }
}