package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class OptionBooleanTest {

  private OptionBoolean vehicleOptionBoolean;

  @Before
  public void setUp() {
    vehicleOptionBoolean = new OptionBoolean(12, "name", "description", true, false);
  }

  @Test
  public void testConstructor() {
    assertEquals(vehicleOptionBoolean.getId(), 12);
    assertEquals(vehicleOptionBoolean.getName(), "name");
    assertTrue(vehicleOptionBoolean.isVariable());
    assertFalse(vehicleOptionBoolean.getValue());
  }

  @Test
  public void testSetters() {
    assertTrue(vehicleOptionBoolean.setValue(true).getValue());
    assertFalse(vehicleOptionBoolean.setValue(false).getValue());
  }

  @Test
  public void testEquals() {
    assertEquals(vehicleOptionBoolean, new OptionBoolean(12, "name", "description", true, false));
    assertNotEquals(vehicleOptionBoolean,
        new OptionBoolean(11, "name", "description", true, false));
    assertNotEquals(vehicleOptionBoolean,
        new OptionBoolean(12, "names", "description", true, false));
    assertNotEquals(vehicleOptionBoolean,
        new OptionBoolean(12, "name", "descriptions", true, false));
    assertNotEquals(vehicleOptionBoolean,
        new OptionBoolean(12, "name", "description", false, false));
    assertNotEquals(vehicleOptionBoolean, new OptionBoolean(12, "name", "description", true, true));
    assertNotEquals(vehicleOptionBoolean, vehicleOptionBoolean.setValue(true));
  }
}