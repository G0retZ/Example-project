package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewStateReadyTest {

  private VehicleOptionsViewStateReady viewState;

  @Mock
  private VehicleOptionsViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    ));
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showVehicleOptionsList(true);
    verify(codeViewActions).showVehicleOptionsPending(false);
    verify(codeViewActions).showVehicleOptionsListErrorMessage(false);
    verify(codeViewActions).setVehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    ));
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    )));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    )));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "humus", false, 1, -1, 2)
        )
    )));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(new ArrayList<>()));
  }
}