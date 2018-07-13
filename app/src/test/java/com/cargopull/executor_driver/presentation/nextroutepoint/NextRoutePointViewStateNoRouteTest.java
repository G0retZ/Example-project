package com.cargopull.executor_driver.presentation.nextroutepoint;

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
  private NextRoutePointViewActions viewActions;

  @Test
  public void testActionsWithTrue() {
    // Дано:
    viewState = new NextRoutePointViewStateNoRoute(true);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showNextRoutePoint("");
    verify(viewActions).showNextRoutePointAddress("", "");
    verify(viewActions).showNextRoutePointComment("");
    verify(viewActions).showNextRoutePointPending(false);
    verify(viewActions).showNoRouteRide(true);
    verify(viewActions).showCloseNextRoutePointAction(false);
    verify(viewActions).showCompleteOrderAction(true);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithFalse() {
    // Дано:
    viewState = new NextRoutePointViewStateNoRoute(false);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showNextRoutePoint("");
    verify(viewActions).showNextRoutePointAddress("", "");
    verify(viewActions).showNextRoutePointComment("");
    verify(viewActions).showNextRoutePointPending(false);
    verify(viewActions).showNoRouteRide(false);
    verify(viewActions).showCloseNextRoutePointAction(false);
    verify(viewActions).showCompleteOrderAction(true);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new NextRoutePointViewStateNoRoute(true);
    assertEquals(viewState, new NextRoutePointViewStateNoRoute(true));
    assertNotEquals(viewState, new NextRoutePointViewStateNoRoute(false));
  }
}