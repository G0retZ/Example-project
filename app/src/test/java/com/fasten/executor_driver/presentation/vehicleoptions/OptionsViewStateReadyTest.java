package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OptionsViewStateReadyTest {

  private VehicleOptionsViewStateReady viewState;

  @Mock
  private VehicleOptionsViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    ));
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(true);
    verify(codeViewActions).showVehicleOptionsList(true);
    verify(codeViewActions).showVehicleOptionsPending(false);
    verify(codeViewActions).showVehicleOptionsListErrorMessage(false);
    verify(codeViewActions).setVehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    ));
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    )));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    )));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "humus", "description", false, 1, -1, 2)
        )
    )));
    assertNotEquals(viewState, new VehicleOptionsViewStateReady(new ArrayList<>()));
  }
}