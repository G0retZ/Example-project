package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    // Действие:
    viewState.apply(viewActions);

    // Результат:
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