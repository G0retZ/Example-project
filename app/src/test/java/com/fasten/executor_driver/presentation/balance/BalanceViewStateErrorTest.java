package com.fasten.executor_driver.presentation.balance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BalanceViewStateErrorTest {

  private BalanceViewStateError viewState;

  @Mock
  private BalanceViewActions nextRoutePointViewActions;
  @Mock
  private ViewState<BalanceViewActions> parentViewState;
  @Mock
  private ViewState<BalanceViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new BalanceViewStateError(parentViewState);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions).showBalanceErrorMessage(true);
    verify(parentViewState, only()).apply(nextRoutePointViewActions);
    verifyNoMoreInteractions(nextRoutePointViewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new BalanceViewStateError(null);

    // Действие:
    viewState.apply(nextRoutePointViewActions);

    // Результат:
    verify(nextRoutePointViewActions).showBalanceErrorMessage(true);
    verifyNoMoreInteractions(nextRoutePointViewActions);
  }

  @Test
  public void testEquals() {
    viewState = new BalanceViewStateError(parentViewState);
    assertEquals(viewState, new BalanceViewStateError(parentViewState));
    assertNotEquals(viewState, new BalanceViewStateError(parentViewState1));
    assertNotEquals(viewState, new BalanceViewStateError(null));
  }
}