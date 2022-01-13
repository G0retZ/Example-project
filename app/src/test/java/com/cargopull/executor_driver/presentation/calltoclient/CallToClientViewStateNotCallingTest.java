package com.cargopull.executor_driver.presentation.calltoclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientViewStateNotCallingTest {

  private CallToClientViewStateNotCalling viewState;

  @Mock
  private CallToClientViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CallToClientViewStateNotCalling();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showCallingToClient(false);
    verify(viewActions).showCallToClientPending(false);
    verifyNoMoreInteractions(viewActions);
  }
}