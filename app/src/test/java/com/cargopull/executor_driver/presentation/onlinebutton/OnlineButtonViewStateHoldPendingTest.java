package com.cargopull.executor_driver.presentation.onlinebutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewStateHoldPendingTest {

  @Mock
  private OnlineButtonViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new OnlineButtonViewStateHoldPending().apply(viewActions);

    // Результат:
    verify(viewActions).enableGoOnlineButton(false);
    verify(viewActions).showGoOnlinePending(true);
    verifyNoMoreInteractions(viewActions);
  }
}