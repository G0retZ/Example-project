package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import org.junit.Before;
import org.junit.Test;

public class VehicleOptionsListItemTest {

  private VehicleOptionsListItem vehicleOptionsListItem;

  @Before
  public void setUp() throws Exception {
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new VehicleOptionBoolean(11, "name", false, false)
    );
  }

  @Test
  public void testGetters() throws Exception {
    assertEquals(vehicleOptionsListItem.getName(), "name");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_boolean);
    assertEquals(vehicleOptionsListItem.getValue(), false);
    assertEquals(vehicleOptionsListItem.getMinValue(), false);
    assertEquals(vehicleOptionsListItem.getMaxValue(), true);
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new VehicleOptionNumeric(11, "emacs", true, 5, 0, 10)
    );
    assertEquals(vehicleOptionsListItem.getName(), "emacs");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_numeric);
    assertEquals(vehicleOptionsListItem.getValue(), 5);
    assertEquals(vehicleOptionsListItem.getMinValue(), 0);
    assertEquals(vehicleOptionsListItem.getMaxValue(), 10);
  }

  @Test
  public void testSetters() throws Exception {
    vehicleOptionsListItem.setValue(true);
    assertEquals(vehicleOptionsListItem.getName(), "name");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_boolean);
    assertEquals(vehicleOptionsListItem.getValue(), true);
    assertEquals(vehicleOptionsListItem.getMinValue(), false);
    assertEquals(vehicleOptionsListItem.getMaxValue(), true);
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new VehicleOptionNumeric(11, "emacs", true, 5, 0, 10)
    );
    vehicleOptionsListItem.setValue(7);
    assertEquals(vehicleOptionsListItem.getName(), "emacs");
    assertEquals(vehicleOptionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_numeric);
    assertEquals(vehicleOptionsListItem.getValue(), 7);
    assertEquals(vehicleOptionsListItem.getMinValue(), 0);
    assertEquals(vehicleOptionsListItem.getMaxValue(), 10);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(11, "name", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(12, "name", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(11, "names", false, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(11, "name", true, false)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(11, "name", false, true)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "emacs", true, 5, 0, 10)
        )
    );
    vehicleOptionsListItem = new VehicleOptionsListItem<>(
        new VehicleOptionNumeric(11, "emacs", true, 5, 0, 10)
    );
    assertEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "emacs", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(12, "emacs", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "name", true, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "emacs", false, 5, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "emacs", true, 0, 0, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "emacs", true, 5, 5, 10)
        )
    );
    assertNotEquals(vehicleOptionsListItem,
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(11, "emacs", true, 5, 0, 5)
        )
    );
  }
}