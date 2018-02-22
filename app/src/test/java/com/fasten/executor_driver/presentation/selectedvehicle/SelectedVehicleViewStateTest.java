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
  private SelectedVehicleViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new SelectedVehicleViewState("");
  }

  @Test
  public void testEmptyNameActions() throws Exception {
    // Действие:
    new SelectedVehicleViewState("").apply(codeViewActions);

    // Результат:
    verify(codeViewActions).setVehicleName("--");
    verify(codeViewActions).enableChangeButton(false);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testNameActions() throws Exception {
    // Действие:
    new SelectedVehicleViewState("name").apply(codeViewActions);

    // Результат:
    verify(codeViewActions).setVehicleName("name");
    verify(codeViewActions).enableChangeButton(true);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new SelectedVehicleViewState(""));
    assertNotEquals(viewState, new SelectedVehicleViewState("1"));
  }
}