package com.fasten.executor_driver.presentation.orderroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderRouteViewStateTest {

  private OrderRouteViewState viewState;

  @Mock
  private OrderRouteViewActions viewActions;
  @Mock
  private RoutePointItem routePointItems;
  @Mock
  private RoutePointItem routePointItems1;

  @Before
  public void setUp() {
    viewState = new OrderRouteViewState(Arrays.asList(routePointItems, routePointItems1));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions)
        .setRoutePointItems(Arrays.asList(routePointItems, routePointItems1));
    verify(viewActions).showOrderRoutePending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState,
        new OrderRouteViewState(Arrays.asList(routePointItems, routePointItems1)));
    assertNotEquals(viewState,
        new OrderRouteViewState(Arrays.asList(routePointItems1, routePointItems)));
  }
}