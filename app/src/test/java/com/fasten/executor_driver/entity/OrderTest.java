package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class OrderTest {

  private Order order;
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    routePoint = new RoutePoint(10, 5, "com", "add");
    order = new Order(7, "com", 1200239, 7000, 2, 2, 20, routePoint);
  }

  @Test
  public void testConstructor() {
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getDistance(), 1200239);
    assertEquals(order.getEstimatedPrice(), 7000);
    assertEquals(order.getPassengers(), 2);
    assertEquals(order.getPorters(), 2);
    assertEquals(order.getTimeout(), 20);
    assertEquals(order.getRoutePoint(), routePoint);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(order, new Order(7, "com", 1200239, 7000, 2, 2, 20, routePoint));
    assertNotEquals(order, new Order(8, "com", 1200239, 7000, 2, 2, 20, routePoint));
    assertNotEquals(order, new Order(7, "co", 1200239, 7000, 2, 2, 20, routePoint));
    assertNotEquals(order, new Order(7, "com", 120023, 7000, 2, 2, 20, routePoint));
    assertNotEquals(order, new Order(7, "com", 1200239, 700, 2, 2, 20, routePoint));
    assertNotEquals(order, new Order(7, "com", 1200239, 7000, 3, 2, 20, routePoint));
    assertNotEquals(order, new Order(7, "com", 1200239, 7000, 2, 5, 20, routePoint));
    assertNotEquals(order, new Order(7, "com", 1200239, 7000, 2, 5, 21, routePoint));
    assertNotEquals(order,
        new Order(7, "com", 1200239, 7000, 2, 2, 20, new RoutePoint(10, 3, "com", "add"))
    );
  }
}