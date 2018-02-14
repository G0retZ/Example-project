package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionItemLimitsTest {

  private ApiVehicleOptionItemLimits apiVehicleOptionItemLimits;

  @Before
  public void setUp() throws Exception {
    apiVehicleOptionItemLimits = new ApiVehicleOptionItemLimits(-1, 10);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicleOptionItemLimits.getMinValue(), -1);
    assertEquals(apiVehicleOptionItemLimits.getMaxValue(), 10);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicleOptionItemLimits, new ApiVehicleOptionItemLimits(-1, 10));
    assertNotEquals(apiVehicleOptionItemLimits, new ApiVehicleOptionItemLimits(0, 10));
    assertNotEquals(apiVehicleOptionItemLimits, new ApiVehicleOptionItemLimits(-1, 5));
  }
}