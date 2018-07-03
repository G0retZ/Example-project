package com.fasten.executor_driver.presentation.nextroutepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NextRoutePointViewStateEnRouteTest {

  private NextRoutePointViewStateEnRoute viewState;

  @Mock
  private NextRoutePointViewActions viewActions;
  @Mock
  private RoutePointItem routePointItem;
  @Mock
  private RoutePointItem routePointItem2;

  @Before
  public void setUp() {
    viewState = new NextRoutePointViewStateEnRoute(routePointItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(routePointItem.getMapUrl()).thenReturn("url");
    when(routePointItem.getAddress()).thenReturn("add");
    when(routePointItem.getComment()).thenReturn("com");
    when(routePointItem.getCoordinatesString()).thenReturn("0,0");

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showNextRoutePoint("url");
    verify(viewActions).showNextRoutePointAddress("0,0", "add");
    verify(viewActions).showNextRoutePointComment("com");
    verify(viewActions).showNextRoutePointPending(false);
    verify(viewActions).showCloseNextRoutePointAction(true);
    verify(viewActions).showCompleteOrderAction(false);
    verify(viewActions).showNoRouteRide(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new NextRoutePointViewStateEnRoute(routePointItem));
    assertNotEquals(viewState, new NextRoutePointViewStateEnRoute(routePointItem2));
  }
}