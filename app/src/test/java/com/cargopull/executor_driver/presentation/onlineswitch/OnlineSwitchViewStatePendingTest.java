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
    // Given:
    viewState = new OnlineSwitchViewStatePending(parentViewState);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).showSwitchPending(true);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testActionsWithNull() {
      // Given:
    viewState = new OnlineSwitchViewStatePending(null);

      // Action:
    viewState.apply(viewActions);

      // Effect:
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