package com.cargopull.executor_driver.presentation.calltooperator;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToOperatorViewStateNotCallingTest {

  private CallToOperatorViewStateNotCalling viewState;

  @Mock
  private CallToOperatorViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CallToOperatorViewStateNotCalling();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showCallingToOperator(false);
  }
}