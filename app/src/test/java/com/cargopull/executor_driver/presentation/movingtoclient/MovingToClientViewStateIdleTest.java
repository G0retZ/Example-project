package com.cargopull.executor_driver.presentation.movingtoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientViewStateIdleTest {

  @Mock
  private MovingToClientViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new MovingToClientViewStateIdle().apply(viewActions);

    // Результат:
    verify(viewActions).showMovingToClientPending(false);
    verify(viewActions).enableMovingToClientCallButton(true);
    verifyNoMoreInteractions(viewActions);
  }
}