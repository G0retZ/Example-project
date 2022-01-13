package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewStateInitialTest {

  private VehicleOptionsViewStateInitial viewState;

  @Mock
  private VehicleOptionsViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new VehicleOptionsViewStateInitial();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

    // Effect:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showVehicleOptionsList(true);
    verify(viewActions).showVehicleOptionsPending(false);
    verify(viewActions).showVehicleOptionsListErrorMessage(false);
    verify(viewActions).setVehicleOptionsListItems(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }
}