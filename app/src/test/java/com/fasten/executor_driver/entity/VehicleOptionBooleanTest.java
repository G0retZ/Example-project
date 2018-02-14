package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class VehicleOptionBooleanTest {

  private VehicleOptionBoolean vehicleOptionBoolean;

  @Before
  public void setUp() throws Exception {
    vehicleOptionBoolean = new VehicleOptionBoolean(12, "name", true, false);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(vehicleOptionBoolean.getId(), 12);
    assertEquals(vehicleOptionBoolean.getName(), "name");
    assertTrue(vehicleOptionBoolean.isVariable());
    assertFalse(vehicleOptionBoolean.getValue());
  }

  @Test
  public void testSetters() throws Exception {
    assertTrue(vehicleOptionBoolean.setValue(true).getValue());
    assertFalse(vehicleOptionBoolean.setValue(false).getValue());
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(vehicleOptionBoolean, new VehicleOptionBoolean(12, "name", true, false));
    assertNotEquals(vehicleOptionBoolean, new VehicleOptionBoolean(11, "name", true, false));
    assertNotEquals(vehicleOptionBoolean, new VehicleOptionBoolean(12, "names", true, false));
    assertNotEquals(vehicleOptionBoolean, new VehicleOptionBoolean(12, "name", false, false));
    assertNotEquals(vehicleOptionBoolean, new VehicleOptionBoolean(12, "name", true, true));
    assertNotEquals(vehicleOptionBoolean, vehicleOptionBoolean.setValue(true));
  }
}