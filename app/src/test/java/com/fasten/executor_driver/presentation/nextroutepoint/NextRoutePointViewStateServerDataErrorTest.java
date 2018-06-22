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
public class NextRoutePointViewStateServerDataErrorTest {

  private NextRoutePointViewStateServerDataError viewState;

  @Mock
  private NextRoutePointViewActions nextRoutePointViewActions;
  @Mock
  private ViewState<NextRoutePointViewActions> parentViewState;
  @Mock
  private ViewState<NextRoutePointViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new NextRoutePointViewStateServerDataError(parentViewState);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions, only()).showNextRoutePointServerDataError();
    verify(parentViewState, only()).apply(nextRoutePointViewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new NextRoutePointViewStateServerDataError(null);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions, only()).showNextRoutePointServerDataError();
  }

  @Test
  public void testEquals() {
    viewState = new NextRoutePointViewStateServerDataError(parentViewState);
    assertEquals(viewState, new NextRoutePointViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new NextRoutePointViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new NextRoutePointViewStateServerDataError(null));
  }
}