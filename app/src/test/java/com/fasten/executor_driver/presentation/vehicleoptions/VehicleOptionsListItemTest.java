package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import org.junit.Before;
import org.junit.Test;

// TODO: написать недостающие тесты.
public class VehicleOptionsListItemTest {

  private VehicleOptionsListItem vehicleOptionsListItem;

  @Before
  public void setUp() {
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new OptionBoolean(11, "name", "description", false, false)
    );
  }

  @Test
  public void testGetters() {
    assertEquals(vehicleOptionsListItem.getName(), "name");
    assertEquals(vehicleOptionsListItem.getDescription(), "description");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_boolean);
    assertEquals(vehicleOptionsListItem.getValue(), false);
    assertEquals(vehicleOptionsListItem.getMinValue(), false);
    assertEquals(vehicleOptionsListItem.getMaxValue(), true);
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new OptionNumeric(11, "emacs", "desc", true, 5, 0, 10)
    );
    assertEquals(vehicleOptionsListItem.getName(), "emacs");
    assertEquals(vehicleOptionsListItem.getDescription(), "desc");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_numeric);
    assertEquals(vehicleOptionsListItem.getValue(), 5);
    assertEquals(vehicleOptionsListItem.getMinValue(), 0);
    assertEquals(vehicleOptionsListItem.getMaxValue(), 10);
  }

  @Test
  public void testSetters() {
    vehicleOptionsListItem.setValue(true);
    assertEquals(vehicleOptionsListItem.getName(), "name");
    assertEquals(vehicleOptionsListItem.getDescription(), "description");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_boolean);
    assertEquals(vehicleOptionsListItem.getValue(), true);
    assertEquals(vehicleOptionsListItem.getMinValue(), false);
    assertEquals(vehicleOptionsListItem.getMaxValue(), true);
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new OptionNumeric(11, "emacs", "desc", true, 5, 0, 10)
    );
    vehicleOptionsListItem.setValue(7);
    assertEquals(vehicleOptionsListItem.getName(), "emacs");
    assertEquals(vehicleOptionsListItem.getDescription(), "desc");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_numeric);
    assertEquals(vehicleOptionsListItem.getValue(), 7);
    assertEquals(vehicleOptionsListItem.getMinValue(), 0);
    assertEquals(vehicleOptionsListItem.getMaxValue(), 10);
  }

  @Test
  public void testEquals() {
    assertEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionBoolean(11, "name", "description", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionBoolean(12, "name", "description", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionBoolean(11, "names", "description", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionBoolean(11, "name", "descriptions", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionBoolean(11, "name", "description", true, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionBoolean(11, "name", "description", false, true)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 0, 10)
        )
    );
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new OptionNumeric(11, "emacs", "description", true, 5, 0, 10)
    );
    assertEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(12, "emacs", "description", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "name", "description", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "descriptions", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", false, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 0, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 5, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 0, 5)
        )
    );
  }
}