package com.fasten.executor_driver.presentation.movingtoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientViewStateCallingTest {

  @Mock
  private MovingToClientViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new MovingToClientViewStateCalling().apply(viewActions);

    // Результат:
    verify(viewActions).showMovingToClientPending(false);
    verify(viewActions).enableMovingToClientCallButton(false);
    verifyNoMoreInteractions(viewActions);
  }
}