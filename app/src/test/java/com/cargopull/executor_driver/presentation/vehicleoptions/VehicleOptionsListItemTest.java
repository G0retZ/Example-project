package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import org.junit.Before;
import org.junit.Test;

public class VehicleOptionsListItemTest {

  private VehicleOptionsListItem vehicleOptionsListItem;

  @Before
  public void setUp() {
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new OptionBoolean(11, "name", "description", false)
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
        new OptionNumeric(11, "emacs", "desc", 5, 0, 10)
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
        new OptionNumeric(11, "emacs", "desc", 5, 0, 10)
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
        new VehicleOptionsListItem<>(new OptionBoolean(11, "name", "description", false))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionBoolean(12, "name", "description", false))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionBoolean(11, "names", "description", false))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionBoolean(11, "name", "descriptions", false))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionBoolean(11, "name", "description", true))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "emacs", "description", 5, 0, 10))
    );
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new OptionNumeric(11, "emacs", "description", 5, 0, 10)
    );
    assertEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "emacs", "description", 5, 0, 10))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(12, "emacs", "description", 5, 0, 10))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "name", "description", 5, 0, 10))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "emacs", "descriptions", 5, 0, 10))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "emacs", "description", 0, 0, 10))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "emacs", "description", 5, 5, 10))
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(new OptionNumeric(11, "emacs", "description", 5, 0, 5))
    );
  }
}