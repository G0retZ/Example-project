package com.cargopull.executor_driver.presentation.nextroutepoint;

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
public class NextRoutePointViewStatePendingTest {

  private NextRoutePointViewStatePending viewState;

  @Mock
  private NextRoutePointViewActions viewActions;
  @Mock
  private ViewState<NextRoutePointViewActions> parentViewState;
  @Mock
  private ViewState<NextRoutePointViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Given:
    viewState = new NextRoutePointViewStatePending(parentViewState);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).showNextRoutePointPending(true);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testActionsWithNull() {
      // Given:
    viewState = new NextRoutePointViewStatePending(null);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).showNextRoutePointPending(true);
  }

  @Test
  public void testEquals() {
    viewState = new NextRoutePointViewStatePending(parentViewState);
    assertEquals(viewState, new NextRoutePointViewStatePending(parentViewState));
    assertNotEquals(viewState, new NextRoutePointViewStatePending(parentViewState1));
    assertNotEquals(viewState, new NextRoutePointViewStatePending(null));
  }
}