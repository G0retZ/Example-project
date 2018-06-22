package com.fasten.executor_driver.presentation.onlineswitch;

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
public class OnlineSwitchViewStateServerDataErrorTest {

  private ViewState<OnlineSwitchViewActions> viewState;

  @Mock
  private OnlineSwitchViewActions onlineSwitchViewActions;
  @Mock
  private ViewState<OnlineSwitchViewActions> parentViewState;
  @Mock
  private ViewState<OnlineSwitchViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new OnlineSwitchViewStateServerDataError(parentViewState);

    // Действие:
    viewState.apply(onlineSwitchViewActions);

    // Результат:
    verify(onlineSwitchViewActions, only()).showOnlineSwitchServerDataError();
    verify(parentViewState, only()).apply(onlineSwitchViewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new OnlineSwitchViewStateServerDataError(null);

    // Действие:
    viewState.apply(onlineSwitchViewActions);

    // Результат:
    verify(onlineSwitchViewActions, only()).showOnlineSwitchServerDataError();
  }

  @Test
  public void testEquals() {
    viewState = new OnlineSwitchViewStateServerDataError(parentViewState);
    assertEquals(viewState, new OnlineSwitchViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new OnlineSwitchViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new OnlineSwitchViewStateServerDataError(null));
  }
}