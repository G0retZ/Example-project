package com.cargopull.executor_driver.presentation.choosevehicle;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChooseVehicleViewStatePendingTest {

  private ChooseVehicleViewStatePending viewState;

  @Mock
  private ChooseVehicleViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ChooseVehicleViewStatePending();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showVehicleListErrorMessage(false);
    verify(viewActions).showVehicleList(false);
    verify(viewActions).showVehicleListPending(true);
    verifyNoMoreInteractions(viewActions);
  }
}