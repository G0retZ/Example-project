package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ApiServiceItemTest {

  private ApiServiceItem apiServiceItem;

  @Before
  public void setUp() {
    apiServiceItem = new ApiServiceItem(0, "name", 1500);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiServiceItem.getId(), 0);
    assertEquals(apiServiceItem.getName(), "name");
    assertEquals(apiServiceItem.getPrice(), new Integer(1500));
    assertFalse(apiServiceItem.isSelected());
  }

  @Test
  public void testSetter() {
    assertFalse(apiServiceItem.isSelected());
    apiServiceItem.setSelected(true);
    assertTrue(apiServiceItem.isSelected());
    apiServiceItem.setSelected(false);
    assertFalse(apiServiceItem.isSelected());
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(apiServiceItem, new ApiServiceItem(0, "name", 1500));
    assertNotEquals(apiServiceItem, new ApiServiceItem(1, "name", 1500));
    assertNotEquals(apiServiceItem, new ApiServiceItem(0, "nam", 1500));
    assertNotEquals(apiServiceItem, new ApiServiceItem(0, null, 1500));
    assertNotEquals(apiServiceItem, new ApiServiceItem(0, "name", 150));
    assertNotEquals(apiServiceItem, new ApiServiceItem(0, "name", null));
    assertNotEquals(apiServiceItem, new ApiServiceItem(0, "name", 1500).setSelected(true));
  }
}