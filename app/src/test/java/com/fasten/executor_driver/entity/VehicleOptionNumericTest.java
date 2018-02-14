package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class VehicleOptionNumericTest {

  private VehicleOptionNumeric vehicleOptionNumeric;

  @Before
  public void setUp() throws Exception {
    vehicleOptionNumeric = new VehicleOptionNumeric(12, "name", true, 30, 5, 31);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(vehicleOptionNumeric.getId(), 12);
    assertEquals(vehicleOptionNumeric.getName(), "name");
    assertEquals(vehicleOptionNumeric.getValue(), new Integer(30));
    assertTrue(vehicleOptionNumeric.isVariable());
    assertEquals(vehicleOptionNumeric.getMinValue(), 5);
    assertEquals(vehicleOptionNumeric.getMaxValue(), 31);
  }

  @Test
  public void testSetters() throws Exception {
    assertEquals(vehicleOptionNumeric.setValue(23).getValue(), new Integer(23));
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(vehicleOptionNumeric, new VehicleOptionNumeric(12, "name", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric, new VehicleOptionNumeric(11, "name", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric, new VehicleOptionNumeric(12, "names", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric, new VehicleOptionNumeric(12, "name", false, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric, new VehicleOptionNumeric(12, "name", true, 23, 5, 31));
    assertNotEquals(vehicleOptionNumeric, new VehicleOptionNumeric(11, "name", true, 30, 8, 31));
    assertNotEquals(vehicleOptionNumeric, new VehicleOptionNumeric(11, "name", true, 0, 5, 50));
    assertNotEquals(vehicleOptionNumeric, vehicleOptionNumeric.setValue(24));
  }
}