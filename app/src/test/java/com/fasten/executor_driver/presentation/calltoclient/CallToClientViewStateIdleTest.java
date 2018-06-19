package com.fasten.executor_driver.presentation.calltoclient;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientViewStateIdleTest {

  private CallToClientViewStateIdle viewState;

  @Mock
  private CallToClientViewActions movingToClientViewActions;

  @Before
  public void setUp() {
    viewState = new CallToClientViewStateIdle();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(movingToClientViewActions);

    // Результат:
    verify(movingToClientViewActions, only()).showCallToClientPending(false);
  }
}