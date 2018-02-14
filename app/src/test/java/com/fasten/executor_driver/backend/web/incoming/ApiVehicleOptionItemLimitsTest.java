package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionItemLimitsTest {

  private ApiVehicleOptionItemLimits apiVehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    apiVehicleOptionItem = new ApiVehicleOptionItemLimits(-1, 10);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicleOptionItem.getMinValue(), -1);
    assertEquals(apiVehicleOptionItem.getMaxValue(), 10);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicleOptionItem, new ApiVehicleOptionItemLimits(-1, 10));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOptionItemLimits(0, 10));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOptionItemLimits(-1, 5));
  }
}