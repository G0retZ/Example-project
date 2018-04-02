package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class OptionNumericTest {

  private OptionNumeric vehicleOptionNumeric;

  @Before
  public void setUp() {
    vehicleOptionNumeric = new OptionNumeric(12, "name", "description", true, 30, 5, 31);
  }

  @Test
  public void testConstructor() {
    assertEquals(vehicleOptionNumeric.getId(), 12);
    assertEquals(vehicleOptionNumeric.getName(), "name");
    assertEquals(vehicleOptionNumeric.getValue(), new Integer(30));
    assertTrue(vehicleOptionNumeric.isVariable());
    assertEquals(vehicleOptionNumeric.getMinValue(), new Integer(5));
    assertEquals(vehicleOptionNumeric.getMaxValue(), new Integer(31));
  }

  @Test
  public void testSetters() {
    assertEquals(vehicleOptionNumeric.setValue(23).getValue(), new Integer(23));
  }

  @Test
  public void testEquals() {
    assertEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "name", "description", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(11, "name", "description", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "names", "description", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "name", "descriptions", true, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "name", "description", false, 30, 5, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "name", "description", true, 23, 5, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "name", "description", true, 30, 8, 31));
    assertNotEquals(vehicleOptionNumeric,
        new OptionNumeric(12, "name", "description", true, 0, 5, 50));
    assertNotEquals(vehicleOptionNumeric, vehicleOptionNumeric.setValue(24));
  }
}