package com.fasten.executor_driver.presentation.options;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OptionsViewStatePendingTest {

  private OptionsViewStatePending viewState;

  @Mock
  private OptionsViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new OptionsViewStatePending();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(false);
    verify(codeViewActions).showVehicleOptionsList(false);
    verify(codeViewActions).showVehicleOptionsPending(true);
    verify(codeViewActions).showVehicleOptionsListErrorMessage(false);
    verifyNoMoreInteractions(codeViewActions);
  }
}