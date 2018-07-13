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
    // Дано:
    viewState = new OnlineSwitchViewState(true);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).checkSwitch(true);
    verify(viewActions).showSwitchPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsFalse() {
    // Дано:
    viewState = new OnlineSwitchViewState(false);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).checkSwitch(false);
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