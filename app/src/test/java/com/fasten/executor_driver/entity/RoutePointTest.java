package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class RoutePointTest {

  private RoutePoint routePoint;

  @Before
  public void setUp() {
    routePoint = new RoutePoint(10, 5, "com", "add");
  }

  @Test
  public void testConstructor() {
    assertEquals(routePoint.getLatitude(), 10, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 5, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "com");
    assertEquals(routePoint.getAddress(), "add");
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(routePoint, new RoutePoint(10, 5, "com", "add"));
    assertNotEquals(routePoint, new RoutePoint(11, 5, "com", "add"));
    assertNotEquals(routePoint, new RoutePoint(10, 6, "com", "add"));
    assertNotEquals(routePoint, new RoutePoint(10, 5, "co", "add"));
    assertNotEquals(routePoint, new RoutePoint(10, 5, "com", "ad"));
  }
}