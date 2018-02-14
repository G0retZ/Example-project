package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionTest {

  private ApiVehicleOption apiVehicleOption;

  @Before
  public void setUp() throws Exception {
    apiVehicleOption = new ApiVehicleOption("option", false, true);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicleOption.getName(), "option");
    assertFalse(apiVehicleOption.isDynamic());
    assertTrue(apiVehicleOption.isNumeric());
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicleOption, new ApiVehicleOption("option", false, true));
    assertNotEquals(apiVehicleOption, new ApiVehicleOption("options", false, true));
    assertNotEquals(apiVehicleOption, new ApiVehicleOption("option", true, true));
    assertNotEquals(apiVehicleOption, new ApiVehicleOption("option", false, false));
  }
}