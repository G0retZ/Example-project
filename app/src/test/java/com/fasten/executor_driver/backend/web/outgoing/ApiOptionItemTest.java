package com.fasten.executor_driver.backend.web.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiOptionItemTest {

  private ApiOptionItem apiOptionItem;

  @Before
  public void setUp() {
    apiOptionItem = new ApiOptionItem(29, "value");
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOptionItem.getId(), 29);
    assertEquals(apiOptionItem.getValue(), "value");
  }

  @Test
  public void testEquals() {
    assertEquals(apiOptionItem, new ApiOptionItem(29, "value"));
    assertNotEquals(apiOptionItem, new ApiOptionItem(18, "value"));
    assertNotEquals(apiOptionItem, new ApiOptionItem(29, "val"));
  }
}