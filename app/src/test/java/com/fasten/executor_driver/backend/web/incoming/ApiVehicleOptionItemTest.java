package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionItemTest {

  private ApiVehicleOptionItem vehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    vehicleOptionItem = new ApiVehicleOptionItem(
        324, "value", new ApiVehicleOptionItemLimits(-5, 123),
        new ApiVehicleOption("option", false, true)
    );
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(vehicleOptionItem.getId(), 324);
    assertEquals(vehicleOptionItem.getValue(), "value");
    assertEquals(vehicleOptionItem.getLimits(), new ApiVehicleOptionItemLimits(-5, 123));
    assertEquals(vehicleOptionItem.getOption(), new ApiVehicleOption("option", false, true));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(32, "value", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "values", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 43),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", null,
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", true, true)));
    assertNotEquals(vehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 123),
            null));
  }
}