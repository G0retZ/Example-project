package com.fasten.executor_driver.presentation.vehicleoptions;

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
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showVehicleOptionsList(true);
    verify(viewActions).showVehicleOptionsPending(true);
    verify(viewActions).showVehicleOptionsListErrorMessage(false);
    verifyNoMoreInteractions(viewActions);
  }
}