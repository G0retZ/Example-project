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
public class OrderRouteViewStateServerDataErrorTest {

  private OrderRouteViewStateServerDataError viewState;

  @Mock
  private OrderRouteViewActions viewActions;
  @Mock
  private ViewState<OrderRouteViewActions> parentViewState;
  @Mock
  private ViewState<OrderRouteViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new OrderRouteViewStateServerDataError(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showOrderRouteServerDataError();
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new OrderRouteViewStateServerDataError(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showOrderRouteServerDataError();
  }

  @Test
  public void testEquals() {
    viewState = new OrderRouteViewStateServerDataError(parentViewState);
    assertEquals(viewState, new OrderRouteViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new OrderRouteViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new OrderRouteViewStateServerDataError(null));
  }
}