package com.cargopull.executor_driver.presentation.movingtoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientViewStatePendingTest {

  @Mock
  private MovingToClientViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new MovingToClientViewStatePending().apply(viewActions);

    // Результат:
    verify(viewActions).showMovingToClientPending(true);
    verify(viewActions).enableMovingToClientCallButton(true);
    verifyNoMoreInteractions(viewActions);
  }
}