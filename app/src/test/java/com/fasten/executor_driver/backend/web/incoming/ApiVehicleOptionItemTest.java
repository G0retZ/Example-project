package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionItemTest {

  private ApiVehicleOptionItem apiVehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    apiVehicleOptionItem = new ApiVehicleOptionItem(324, "value",
        new ApiVehicleOptionItemLimits(-5, 123),
        new ApiVehicleOption("option", false, true));
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicleOptionItem.getId(), 324);
    assertEquals(apiVehicleOptionItem.getValue(), "value");
    assertEquals(apiVehicleOptionItem.getLimits(), new ApiVehicleOptionItemLimits(-5, 123));
    assertEquals(apiVehicleOptionItem.getOption(), new ApiVehicleOption("option", false, true));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(32, "value", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(324, "values", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 43),
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", null,
            new ApiVehicleOption("option", false, true)));
    assertNotEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option", true, true)));
    assertNotEquals(apiVehicleOptionItem,
        new ApiVehicleOptionItem(324, "value", new ApiVehicleOptionItemLimits(-5, 123),
            null));
  }
}