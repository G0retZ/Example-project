package com.fasten.executor_driver.presentation.vehicleoptions;

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

  private VehicleOptionsViewStateError viewState;

  @Mock
  private VehicleOptionsViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new VehicleOptionsViewStateError(123);
  }

  @Test
  public void testActions() throws Exception {
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
  public void testEquals() throws Exception {
    assertEquals(viewState, new VehicleOptionsViewStateError(123));
    assertNotEquals(viewState, new VehicleOptionsViewStateError(0));
  }
}