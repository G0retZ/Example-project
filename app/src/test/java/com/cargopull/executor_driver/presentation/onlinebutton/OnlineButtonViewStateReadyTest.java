package com.cargopull.executor_driver.presentation.onlinebutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewStateReadyTest {

  @Mock
  private OnlineButtonViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new OnlineButtonViewStateReady().apply(viewActions);

    // Результат:
    verify(viewActions).enableGoOnlineButton(true);
    verify(viewActions).showGoOnlinePending(false);
    verifyNoMoreInteractions(viewActions);
  }
}