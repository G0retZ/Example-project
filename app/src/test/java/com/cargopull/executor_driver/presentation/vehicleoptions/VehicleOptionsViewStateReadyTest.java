package com.cargopull.executor_driver.presentation.vehicleoptions;

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
public class VehicleOptionsViewStateReadyTest {

  private VehicleOptionsViewStateReady viewState;

  @Mock
  private VehicleOptionsViewActions viewActions;
  @Mock
  private VehicleOptionsListItems vehicleOptionsListItems;
  @Mock
  private VehicleOptionsListItems vehicleOptionsListItems1;

  @Before
  public void setUp() {
    viewState = new VehicleOptionsViewStateReady(vehicleOptionsListItems);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(true);
    verify(viewActions).showVehicleOptionsList(true);
    verify(viewActions).showVehicleOptionsPending(false);
    verify(viewActions).showVehicleOptionsListErrorMessage(false);
    verify(viewActions).setVehicleOptionsListItems(vehicleOptionsListItems);
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new VehicleOptionsViewStateReady(vehicleOptionsListItems));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(vehicleOptionsListItems1));
    assertNotEquals(viewState, null);
    assertNotEquals(viewState, "");
  }
}