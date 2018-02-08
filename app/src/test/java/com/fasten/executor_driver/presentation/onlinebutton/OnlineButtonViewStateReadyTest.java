package com.fasten.executor_driver.presentation.onlinebutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewStateReadyTest {

  private OnlineButtonViewStateReady viewState;

  @Mock
  private OnlineButtonViewActions onlineButtonViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new OnlineButtonViewStateReady();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(onlineButtonViewActions);

    // Результат:
    verify(onlineButtonViewActions).enableGoOnlineButton(true);
    verify(onlineButtonViewActions).showGoOnlineError(null);
    verifyNoMoreInteractions(onlineButtonViewActions);
  }
}