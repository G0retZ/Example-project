package com.fasten.executor_driver.presentation.choosevehicle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.Vehicle;
import org.junit.Before;
import org.junit.Test;

public class ChooseVehicleListItemTest {

  private ChooseVehicleListItem chooseVehicleListItem;

  @Before
  public void setUp() throws Exception {
    chooseVehicleListItem = new ChooseVehicleListItem(
        new Vehicle(11, "manufacturer", "model", "color", "lic plate", false)
    );
  }

  @Test
  public void testGetters() throws Exception {
    assertEquals(chooseVehicleListItem.getName(), "manufacturer model (lic plate)");
    assertTrue(chooseVehicleListItem.isSelectable());
    assertEquals(chooseVehicleListItem.getLabel(), R.string.free);
    chooseVehicleListItem = new ChooseVehicleListItem(
        new Vehicle(11, "manufacturer", "model", "color", "lic plate", true)
    );
    assertFalse(chooseVehicleListItem.isSelectable());
    assertEquals(chooseVehicleListItem.getLabel(), R.string.busy);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(11, "manufacturer", "model", "color", "lic plate", false)
        )
    );
    assertNotEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(12, "manufacturer", "model", "color", "lic plate", false)
        )
    );
    assertNotEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(11, "manufacturers", "model", "color", "lic plate", false)
        )
    );
    assertNotEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(11, "manufacturer", "models", "color", "lic plate", false)
        )
    );
    assertNotEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(11, "manufacturer", "model", "colors", "lic plate", false)
        )
    );
    assertNotEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(11, "manufacturer", "model", "color", "lic plates", false)
        )
    );
    assertNotEquals(chooseVehicleListItem,
        new ChooseVehicleListItem(
            new Vehicle(11, "manufacturer", "model", "color", "lic plate", true)
        )
    );
  }
}