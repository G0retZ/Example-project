package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewStateReadyTest {

  private VehicleOptionsViewStateReady viewState;

  @Mock
  private VehicleOptionsViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new VehicleOptionsViewStateReady(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    )));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(true);
    verify(viewActions).showVehicleOptionsList(true);
    verify(viewActions).showVehicleOptionsPending(false);
    verify(viewActions).showVehicleOptionsListErrorMessage(false);
    verify(viewActions).setVehicleOptionsListItems(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    )));
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState,
        new VehicleOptionsViewStateReady(new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "description", true, true)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "description", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
            )
        ))));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "description", true, true)
            )
        ), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    ))));
    assertNotEquals(viewState,
        new VehicleOptionsViewStateReady(new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "description", true, true)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "description", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "humus", "description", false, 1, -1, 2)
            )
        ))));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(new VehicleOptionsListItems(
        new ArrayList<>(), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    ))));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(new VehicleOptionsListItems(
        Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "description", true, true)
            )
        ), new ArrayList<>()
    )));
  }
}