package com.cargopull.executor_driver.presentation.calltoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientViewStateCallingTest {

  private CallToClientViewStateCalling viewState;

  @Mock
  private CallToClientViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CallToClientViewStateCalling();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showCallingToClient(true);
    verify(viewActions).showCallToClientPending(false);
    verifyNoMoreInteractions(viewActions);
  }
}