package com.fasten.executor_driver.presentation.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OptionsViewStateErrorTest {

  private OptionsViewStateError viewState;

  @Mock
  private OptionsViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new OptionsViewStateError(123);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(false);
    verify(codeViewActions).showVehicleOptionsList(false);
    verify(codeViewActions).showVehicleOptionsPending(false);
    verify(codeViewActions).showVehicleOptionsListErrorMessage(true);
    verify(codeViewActions).setVehicleOptionsListErrorMessage(123);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OptionsViewStateError(123));
    assertNotEquals(viewState, new OptionsViewStateError(0));
  }
}