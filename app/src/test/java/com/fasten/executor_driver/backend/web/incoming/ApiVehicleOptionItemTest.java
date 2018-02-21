package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionItemTest {

  private ApiVehicleOptionItem vehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    vehicleOptionItem = new ApiVehicleOptionItem(324, "option", true, false, "value", -5, 123);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(vehicleOptionItem.getId(), 324);
    assertEquals(vehicleOptionItem.getName(), "option");
    assertTrue(vehicleOptionItem.isNumeric());
    assertFalse(vehicleOptionItem.isDynamic());
    assertEquals(vehicleOptionItem.getValue(), "value");
    assertEquals(vehicleOptionItem.getMinValue(), new Integer(-5));
    assertEquals(vehicleOptionItem.getMaxValue(), new Integer(123));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(32, "option", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "options", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, null, true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", false, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, true, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, "values", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, null, -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, "value", 0, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, "value", null, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, "value", -5, 0));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "option", true, false, "value", -5, null));
  }
}