package com.fasten.executor_driver.presentation.nextroutepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NextRoutePointViewStateNoRouteTest {

  private NextRoutePointViewStateNoRoute viewState;

  @Mock
  private NextRoutePointViewActions nextRoutePointViewActions;

  @Test
  public void testActionsWithTrue() {
    // Дано:
    viewState = new NextRoutePointViewStateNoRoute(true);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions).showNextRoutePoint("");
    verify(nextRoutePointViewActions).showNextRoutePointCoordinates("");
    verify(nextRoutePointViewActions).showNextRoutePointAddress("");
    verify(nextRoutePointViewActions).showNextRoutePointComment("");
    verify(nextRoutePointViewActions).showNextRoutePointPending(false);
    verify(nextRoutePointViewActions).showNextRoutePointNetworkErrorMessage(false);
    verify(nextRoutePointViewActions).showNoRouteRide(true);
    verify(nextRoutePointViewActions).showCloseNextRoutePointAction(false);
    verify(nextRoutePointViewActions).showCompleteOrderAction(true);
    verifyNoMoreInteractions(nextRoutePointViewActions);
  }

  @Test
  public void testActionsWithFalse() {
    // Дано:
    viewState = new NextRoutePointViewStateNoRoute(false);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions).showNextRoutePoint("");
    verify(nextRoutePointViewActions).showNextRoutePointCoordinates("");
    verify(nextRoutePointViewActions).showNextRoutePointAddress("");
    verify(nextRoutePointViewActions).showNextRoutePointComment("");
    verify(nextRoutePointViewActions).showNextRoutePointPending(false);
    verify(nextRoutePointViewActions).showNextRoutePointNetworkErrorMessage(false);
    verify(nextRoutePointViewActions).showNoRouteRide(false);
    verify(nextRoutePointViewActions).showCloseNextRoutePointAction(false);
    verify(nextRoutePointViewActions).showCompleteOrderAction(true);
    verifyNoMoreInteractions(nextRoutePointViewActions);
  }

  @Test
  public void testEquals() {
    viewState = new NextRoutePointViewStateNoRoute(true);
    assertEquals(viewState, new NextRoutePointViewStateNoRoute(true));
    assertNotEquals(viewState, new NextRoutePointViewStateNoRoute(false));
  }
}