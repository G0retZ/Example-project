package com.cargopull.executor_driver.presentation.balance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BalanceViewStatePendingTest {

  private BalanceViewStatePending viewState;

  @Mock
  private BalanceViewActions viewActions;
  @Mock
  private ViewState<BalanceViewActions> parentViewState;
  @Mock
  private ViewState<BalanceViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new BalanceViewStatePending(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showBalancePending(true);
    verify(parentViewState, only()).apply(viewActions);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new BalanceViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showBalancePending(true);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new BalanceViewStatePending(parentViewState);
    assertEquals(viewState, new BalanceViewStatePending(parentViewState));
    assertNotEquals(viewState, new BalanceViewStatePending(parentViewState1));
    assertNotEquals(viewState, new BalanceViewStatePending(null));
  }
}