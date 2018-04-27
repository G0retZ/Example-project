package com.fasten.executor_driver.presentation.onlineswitch;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineSwitchViewStateUnCheckedSocketErrorTest {

  private ViewState<OnlineSwitchViewActions> viewState;

  @Mock
  private OnlineSwitchViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new OnlineSwitchViewStateUnCheckedSocketError();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).checkSwitch(false);
    verify(codeViewActions).showSwitchPending(false);
    verify(codeViewActions).showError(R.string.no_network_connection);
    verifyNoMoreInteractions(codeViewActions);
  }
}