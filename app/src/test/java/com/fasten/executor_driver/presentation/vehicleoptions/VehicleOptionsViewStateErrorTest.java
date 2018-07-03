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
public class VehicleOptionsViewStateErrorTest {

  private VehicleOptionsViewStateError viewState;

  @Mock
  private VehicleOptionsViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new VehicleOptionsViewStateError(123);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showVehicleOptionsList(false);
    verify(viewActions).showVehicleOptionsPending(false);
    verify(viewActions).showVehicleOptionsListErrorMessage(true);
    verify(viewActions).setVehicleOptionsListErrorMessage(123);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new VehicleOptionsViewStateError(123));
    assertNotEquals(viewState, new VehicleOptionsViewStateError(0));
  }
}