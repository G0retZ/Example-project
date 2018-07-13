package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ServiceTest {

  private Service service;

  @Before
  public void setUp() {
    service = new Service(10, "name", 200, false);
  }

  @Test
  public void testConstructor() {
    assertEquals(service.getId(), 10);
    assertEquals(service.getName(), "name");
    assertEquals(service.getPrice(), 200);
    assertFalse(service.isSelected());
  }

  @Test
  public void testSetters() {
    assertTrue(service.setSelected(true).isSelected());
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(service, new Service(10, "name", 200, false));
    assertNotEquals(service, new Service(1, "name", 200, false));
    assertNotEquals(service, new Service(10, "nam", 200, false));
    assertNotEquals(service, new Service(10, "name", 20, false));
    assertNotEquals(service, new Service(10, "name", 200, true));
  }
}