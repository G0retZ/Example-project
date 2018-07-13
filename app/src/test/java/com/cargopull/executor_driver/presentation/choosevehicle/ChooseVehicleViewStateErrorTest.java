package com.cargopull.executor_driver.presentation.choosevehicle;

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
public class ChooseVehicleViewStateErrorTest {

  private ChooseVehicleViewStateError viewState;

  @Mock
  private ChooseVehicleViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ChooseVehicleViewStateError(123);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showVehicleList(false);
    verify(viewActions).showVehicleListPending(false);
    verify(viewActions).showVehicleListErrorMessage(true);
    verify(viewActions).setVehicleListErrorMessage(123);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ChooseVehicleViewStateError(123));
    assertNotEquals(viewState, new ChooseVehicleViewStateError(0));
  }
}