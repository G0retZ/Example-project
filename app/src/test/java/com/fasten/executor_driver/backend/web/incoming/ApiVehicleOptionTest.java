package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionTest {

  private ApiVehicleOption apiVehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    apiVehicleOptionItem = new ApiVehicleOption("option", false, true);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicleOptionItem.getName(), "option");
    assertFalse(apiVehicleOptionItem.isDynamic());
    assertTrue(apiVehicleOptionItem.isNumeric());
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicleOptionItem, new ApiVehicleOption("option", false, true));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOption("options", false, true));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOption("option", true, true));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOption("option", false, false));
  }
}