package com.fasten.executor_driver.presentation.onlinebutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewStateHoldTest {

  private OnlineButtonViewStateHold viewState;

  @Mock
  private OnlineButtonViewActions smsButtonViewActions;

  @Before
  public void setUp() {
    viewState = new OnlineButtonViewStateHold();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(smsButtonViewActions);

    // Результат:
    verify(smsButtonViewActions).enableGoOnlineButton(false);
    verify(smsButtonViewActions).showGoOnlineError(null);
    verifyNoMoreInteractions(smsButtonViewActions);
  }
}