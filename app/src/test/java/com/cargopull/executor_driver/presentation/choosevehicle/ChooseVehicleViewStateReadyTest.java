package com.cargopull.executor_driver.presentation.choosevehicle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.entity.Vehicle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class ChooseVehicleViewStateReadyTest {

    private ChooseVehicleViewStateReady viewState;

    @Mock
    private ChooseVehicleViewActions viewActions;

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
        // Action:
    viewState.apply(viewActions);

        // Effect:
    verify(viewActions).showVehicleList(true);
    verify(viewActions).showVehicleListPending(false);
    verify(viewActions).showVehicleListErrorMessage(false);
    verify(viewActions).setVehicleListItems(Arrays.asList(
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
    verifyNoMoreInteractions(viewActions);
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