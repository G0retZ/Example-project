package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewStatePendingTest {

  private VehicleOptionsViewStatePending viewState;

  @Mock
  private VehicleOptionsViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new VehicleOptionsViewStatePending();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showVehicleOptionsList(true);
    verify(viewActions).showVehicleOptionsPending(true);
    verify(viewActions).showVehicleOptionsListErrorMessage(false);
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }
}