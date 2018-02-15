package com.fasten.executor_driver.backend.web.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiVehicleOptionItemTest {

  private ApiVehicleOptionItem apiVehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    apiVehicleOptionItem = new ApiVehicleOptionItem(29, "value");
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicleOptionItem.getId(), 29);
    assertEquals(apiVehicleOptionItem.getValue(), "value");
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicleOptionItem, new ApiVehicleOptionItem(29, "value"));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOptionItem(18, "value"));
    assertNotEquals(apiVehicleOptionItem, new ApiVehicleOptionItem(29, "val"));
  }
}