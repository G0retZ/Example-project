package com.fasten.executor_driver.presentation.movingtoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientViewStatePendingTest {

  @Mock
  private MovingToClientViewActions movingToClientViewActions;

  @Test
  public void testActions() {
    // Действие:
    new MovingToClientViewStatePending().apply(movingToClientViewActions);

    // Результат:
    verify(movingToClientViewActions).showMovingToClientPending(true);
    verify(movingToClientViewActions).enableMovingToClientCallButton(true);
    verifyNoMoreInteractions(movingToClientViewActions);
  }
}