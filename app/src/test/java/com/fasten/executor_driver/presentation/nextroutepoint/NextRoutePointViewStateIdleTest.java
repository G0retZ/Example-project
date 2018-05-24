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
public class NextRoutePointViewStateIdleTest {

  private NextRoutePointViewStateIdle viewState;

  @Mock
  private NextRoutePointViewActions nextRoutePointViewActions;
  @Mock
  private RoutePointItem routePointItem;
  @Mock
  private RoutePointItem routePointItem2;

  @Before
  public void setUp() {
    viewState = new NextRoutePointViewStateIdle(routePointItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(routePointItem.getMapUrl()).thenReturn("url");
    when(routePointItem.getAddress()).thenReturn("add");
    when(routePointItem.getComment()).thenReturn("com");
    when(routePointItem.getCoordinatesString()).thenReturn("0,0");

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions).showNextRoutePoint("url");
    verify(nextRoutePointViewActions).showNextRoutePointCoordinates("0,0");
    verify(nextRoutePointViewActions).showNextRoutePointAddress("add");
    verify(nextRoutePointViewActions).showNextRoutePointComment("com");
    verify(nextRoutePointViewActions).showNextRoutePointPending(false);
    verify(nextRoutePointViewActions).showNextRoutePointNetworkErrorMessage(false);
    verifyNoMoreInteractions(nextRoutePointViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new NextRoutePointViewStateIdle(routePointItem));
    assertNotEquals(viewState, new NextRoutePointViewStateIdle(routePointItem2));
  }
}