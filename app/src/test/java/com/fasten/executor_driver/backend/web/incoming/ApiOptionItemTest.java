package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ApiOptionItemTest {

  private ApiOptionItem vehicleOptionItem;

  @Before
  public void setUp() throws Exception {
    vehicleOptionItem = new ApiOptionItem(324, "option", "description", true, false, "value", -5,
        123);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(vehicleOptionItem.getId(), 324);
    assertEquals(vehicleOptionItem.getName(), "option");
    assertTrue(vehicleOptionItem.isNumeric());
    assertFalse(vehicleOptionItem.isDynamic());
    assertEquals(vehicleOptionItem.getValue(), "value");
    assertEquals(vehicleOptionItem.getMinValue(), new Integer(-5));
    assertEquals(vehicleOptionItem.getMaxValue(), new Integer(123));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(32, "option", "description", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "options", "description", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, null, "description", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "descriptions", true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", null, true, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", false, false, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, true, "value", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, "values", -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, null, -5, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, "value", 0, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, "value", null, 123));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, "value", -5, 0));
    assertNotEquals(vehicleOptionItem,
        new ApiOptionItem(324, "option", "description", true, false, "value", -5, null));
  }
}