package com.cargopull.executor_driver.presentation.onlineswitch;

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
public class OnlineSwitchViewStatePendingTest {

  private ViewState<OnlineSwitchViewActions> viewState;

  @Mock
  private OnlineSwitchViewActions viewActions;
  @Mock
  private ViewState<OnlineSwitchViewActions> parentViewState;
  @Mock
  private ViewState<OnlineSwitchViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new OnlineSwitchViewStatePending(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showSwitchPending(true);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new OnlineSwitchViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showSwitchPending(true);
  }

  @Test
  public void testEquals() {
    viewState = new OnlineSwitchViewStatePending(parentViewState);
    assertEquals(viewState, new OnlineSwitchViewStatePending(parentViewState));
    assertNotEquals(viewState, new OnlineSwitchViewStatePending(parentViewState1));
    assertNotEquals(viewState, new OnlineSwitchViewStatePending(null));
  }
}