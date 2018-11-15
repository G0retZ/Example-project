package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewStateResolvableErrorTest {

  private VehicleOptionsViewStateResolvableError viewState;

  @Mock
  private VehicleOptionsViewActions viewActions;
  @Mock
  private ViewState<VehicleOptionsViewActions> parentViewState;
  @Mock
  private ViewState<VehicleOptionsViewActions> parentViewState1;
  @Mock
  private Runnable action;
  @Mock
  private Runnable action1;

  @Before
  public void setUp() {
    viewState = new VehicleOptionsViewStateResolvableError(123, parentViewState, action);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(parentViewState, only()).apply(viewActions);
    verify(viewActions).showPersistentDialog(123, action);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState,
        new VehicleOptionsViewStateResolvableError(123, parentViewState, action));
    assertEquals(viewState,
        new VehicleOptionsViewStateResolvableError(123, parentViewState, action1));
    assertNotEquals(viewState,
        new VehicleOptionsViewStateResolvableError(0, parentViewState, action));
    assertNotEquals(viewState,
        new VehicleOptionsViewStateResolvableError(123, parentViewState1, action));
    assertNotEquals(viewState, null);
    assertNotEquals(viewState, "");
  }
}