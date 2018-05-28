package com.fasten.executor_driver.presentation.nextroutepoint;

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
public class NextRoutePointViewStateErrorTest {

  private NextRoutePointViewStateError viewState;

  @Mock
  private NextRoutePointViewActions nextRoutePointViewActions;
  @Mock
  private ViewState<NextRoutePointViewActions> parentViewState;
  @Mock
  private ViewState<NextRoutePointViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new NextRoutePointViewStateError(parentViewState);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions, only()).showNextRoutePointNetworkErrorMessage(true);
    verify(parentViewState, only()).apply(nextRoutePointViewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new NextRoutePointViewStateError(null);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions, only()).showNextRoutePointNetworkErrorMessage(true);
  }

  @Test
  public void testEquals() {
    viewState = new NextRoutePointViewStateError(parentViewState);
    assertEquals(viewState, new NextRoutePointViewStateError(parentViewState));
    assertNotEquals(viewState, new NextRoutePointViewStateError(parentViewState1));
    assertNotEquals(viewState, new NextRoutePointViewStateError(null));
  }
}