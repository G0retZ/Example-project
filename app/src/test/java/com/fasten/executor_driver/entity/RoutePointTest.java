package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RoutePointTest {

  private RoutePoint routePoint;

  @Before
  public void setUp() {
    routePoint = new RoutePoint(3, 10, 5, "com", "add", true);
  }

  @Test
  public void testConstructor() {
    assertEquals(routePoint.getId(), 3);
    assertEquals(routePoint.getLatitude(), 10, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 5, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "com");
    assertEquals(routePoint.getAddress(), "add");
    assertTrue(routePoint.isChecked());
  }
}