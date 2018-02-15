package com.fasten.executor_driver.presentation.choosevehicle;

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
  private ChooseVehicleViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new ChooseVehicleViewStateError(123);
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showVehicleList(false);
    verify(codeViewActions).showVehicleListPending(false);
    verify(codeViewActions).showVehicleListErrorMessage(true);
    verify(codeViewActions).setVehicleListErrorMessage(123);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new ChooseVehicleViewStateError(123));
    assertNotEquals(viewState, new ChooseVehicleViewStateError(0));
  }
}