package com.cargopull.executor_driver.presentation.onlineswitch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineSwitchViewStateTest {

  private ViewState<OnlineSwitchViewActions> viewState;

  @Mock
  private OnlineSwitchViewActions viewActions;

  @Test
  public void testActionsTrue() {
    // Given:
    viewState = new OnlineSwitchViewState(true);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showTakeBreakButton(true);
    verify(viewActions).showBreakText(false);
    verify(viewActions).showResumeWorkButton(false);
    verify(viewActions).showSwitchPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsFalse() {
      // Given:
    viewState = new OnlineSwitchViewState(false);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showTakeBreakButton(false);
    verify(viewActions).showBreakText(true);
    verify(viewActions).showResumeWorkButton(true);
    verify(viewActions).showSwitchPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new OnlineSwitchViewState(true);
    assertEquals(viewState, new OnlineSwitchViewState(true));
    assertNotEquals(viewState, new OnlineSwitchViewState(false));
  }
}