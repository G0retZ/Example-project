package com.fasten.executor_driver.presentation.onlineswitch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineSwitchViewStateTest {

  private ViewState<OnlineSwitchViewActions> viewState;

  @Mock
  private OnlineSwitchViewActions codeViewActions;

  @Test
  public void testActionsTrue() {
    // Дано:
    viewState = new OnlineSwitchViewState(true);

    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).checkSwitch(true);
    verify(codeViewActions).showSwitchPending(false);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testActionsFalse() {
    // Дано:
    viewState = new OnlineSwitchViewState(false);

    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).checkSwitch(false);
    verify(codeViewActions).showSwitchPending(false);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() {
    viewState = new OnlineSwitchViewState(true);
    assertEquals(viewState, new OnlineSwitchViewState(true));
    assertNotEquals(viewState, new OnlineSwitchViewState(false));
  }
}