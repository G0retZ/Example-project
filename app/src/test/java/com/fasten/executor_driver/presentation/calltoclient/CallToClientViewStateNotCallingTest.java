package com.fasten.executor_driver.presentation.calltoclient;

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
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showCallingToClient(false);
    verify(viewActions).showCallToClientPending(false);
    verifyNoMoreInteractions(viewActions);
  }
}