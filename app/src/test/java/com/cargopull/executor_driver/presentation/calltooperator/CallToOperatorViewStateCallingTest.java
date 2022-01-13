package com.cargopull.executor_driver.presentation.calltooperator;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToOperatorViewStateCallingTest {

  private CallToOperatorViewStateCalling viewState;

  @Mock
  private CallToOperatorViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CallToOperatorViewStateCalling();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions, only()).showCallingToOperator(true);
  }
}