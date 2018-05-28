package com.fasten.executor_driver.presentation.orderroute;

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
public class OrderRouteViewStateTest {

  private OrderRouteViewState viewState;

  @Mock
  private OrderRouteViewActions orderRouteViewActions;
  @Mock
  private RoutePointItems routePointItems;
  @Mock
  private RoutePointItems routePointItems1;

  @Before
  public void setUp() {
    viewState = new OrderRouteViewState(routePointItems);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderRouteViewActions);

    // Результат:
    verify(orderRouteViewActions).setRoutePointItems(routePointItems);
    verify(orderRouteViewActions).showOrderRouteErrorMessage(false);
    verify(orderRouteViewActions).showOrderRoutePending(false);
    verifyNoMoreInteractions(orderRouteViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderRouteViewState(routePointItems));
    assertNotEquals(viewState, new OrderRouteViewState(routePointItems1));
  }
}