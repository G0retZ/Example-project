package com.fasten.executor_driver.presentation.selectedvehicle;

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
public class SelectedVehicleViewStateTest {

  private SelectedVehicleViewState viewState;

  @Mock
  private SelectedVehicleViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new SelectedVehicleViewState("");
  }

  @Test
  public void testEmptyNameActions() {
    // Действие:
    new SelectedVehicleViewState("").apply(viewActions);

    // Результат:
    verify(viewActions).setVehicleName("--");
    verify(viewActions).enableChangeButton(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testNameActions() {
    // Действие:
    new SelectedVehicleViewState("name").apply(viewActions);

    // Результат:
    verify(viewActions).setVehicleName("name");
    verify(viewActions).enableChangeButton(true);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new SelectedVehicleViewState(""));
    assertNotEquals(viewState, new SelectedVehicleViewState("1"));
  }
}