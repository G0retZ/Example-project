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
public class BalanceViewStateServerDataErrorTest {

  private BalanceViewStateServerDataError viewState;

  @Mock
  private BalanceViewActions viewActions;
  @Mock
  private ViewState<BalanceViewActions> parentViewState;
  @Mock
  private ViewState<BalanceViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new BalanceViewStateServerDataError(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showBalanceServerDataError();
    verify(parentViewState, only()).apply(viewActions);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new BalanceViewStateServerDataError(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showBalanceServerDataError();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new BalanceViewStateServerDataError(parentViewState);
    assertEquals(viewState, new BalanceViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new BalanceViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new BalanceViewStateServerDataError(null));
  }
}