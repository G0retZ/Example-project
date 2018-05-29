package com.fasten.executor_driver.presentation.calltoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientViewStateNetworkErrorTest {

  private CallToClientViewStateNetworkError viewState;

  @Mock
  private CallToClientViewActions movingToClientViewActions;

  @Before
  public void setUp() {
    viewState = new CallToClientViewStateNetworkError();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(movingToClientViewActions);

    // Результат:
    verify(movingToClientViewActions).showCallToClientPending(false);
    verify(movingToClientViewActions).showNetworkErrorMessage(true);
    verifyNoMoreInteractions(movingToClientViewActions);
  }
}