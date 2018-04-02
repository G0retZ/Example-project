package com.fasten.executor_driver.presentation.choosevehicle;

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
  private ChooseVehicleViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new ChooseVehicleViewStatePending();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showVehicleListErrorMessage(false);
    verify(codeViewActions).showVehicleList(false);
    verify(codeViewActions).showVehicleListPending(true);
    verifyNoMoreInteractions(codeViewActions);
  }
}