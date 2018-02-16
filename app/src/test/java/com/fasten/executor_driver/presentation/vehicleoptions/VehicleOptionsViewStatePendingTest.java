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
  private VehicleOptionsViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new VehicleOptionsViewStatePending();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showVehicleOptionsList(false);
    verify(codeViewActions).showVehicleOptionsPending(true);
    verifyNoMoreInteractions(codeViewActions);
  }
}