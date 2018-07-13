package com.cargopull.executor_driver.presentation.onlinebutton;

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
  private OnlineButtonViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new OnlineButtonViewStateReady();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableGoOnlineButton(true);
    verify(viewActions).showGoOnlineError(null);
    verifyNoMoreInteractions(viewActions);
  }
}