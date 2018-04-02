package com.fasten.executor_driver.presentation.choosevehicle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.entity.Vehicle;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChooseVehicleViewStateReadyTest {

  private ChooseVehicleViewStateReady viewState;

  @Mock
  private ChooseVehicleViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new ChooseVehicleViewStateReady(Arrays.asList(
        new ChooseVehicleListItem(
            new Vehicle(1, "m", "m", "c", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(2, "ma", "m", "co", "l", true)
        ),
        new ChooseVehicleListItem(
            new Vehicle(3, "m", "m", "co", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(4, "ma", "m", "c", "l", true)
        )
    ));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showVehicleList(true);
    verify(codeViewActions).showVehicleListPending(false);
    verify(codeViewActions).showVehicleListErrorMessage(false);
    verify(codeViewActions).setVehicleListItems(Arrays.asList(
        new ChooseVehicleListItem(
            new Vehicle(1, "m", "m", "c", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(2, "ma", "m", "co", "l", true)
        ),
        new ChooseVehicleListItem(
            new Vehicle(3, "m", "m", "co", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(4, "ma", "m", "c", "l", true)
        )
    ));
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ChooseVehicleViewStateReady(Arrays.asList(
        new ChooseVehicleListItem(
            new Vehicle(1, "m", "m", "c", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(2, "ma", "m", "co", "l", true)
        ),
        new ChooseVehicleListItem(
            new Vehicle(3, "m", "m", "co", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(4, "ma", "m", "c", "l", true)
        )
    )));
    assertNotEquals(viewState, new ChooseVehicleViewStateReady(Arrays.asList(
        new ChooseVehicleListItem(
            new Vehicle(2, "ma", "m", "co", "l", true)
        ),
        new ChooseVehicleListItem(
            new Vehicle(3, "m", "m", "co", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(4, "ma", "m", "c", "l", true)
        )
    )));
    assertNotEquals(viewState, new ChooseVehicleViewStateReady(Arrays.asList(
        new ChooseVehicleListItem(
            new Vehicle(1, "m", "m", "c", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(2, "ma", "m", "co", "l", true)
        ),
        new ChooseVehicleListItem(
            new Vehicle(3, "m", "m", "co", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(5, "ma", "m", "c", "l", true)
        )
    )));
    assertNotEquals(viewState, new ChooseVehicleViewStateReady(new ArrayList<>()));
  }
}