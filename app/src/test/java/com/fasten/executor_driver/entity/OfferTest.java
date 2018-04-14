package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class OfferTest {

  private Offer offer;
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    routePoint = new RoutePoint(10, 5, "com", "add");
    offer = new Offer(7, "com", 1200239, 7000, 2, 2, 20, routePoint);
  }

  @Test
  public void testConstructor() {
    assertEquals(offer.getId(), 7);
    assertEquals(offer.getComment(), "com");
    assertEquals(offer.getDistance(), 1200239);
    assertEquals(offer.getEstimatedPrice(), 7000);
    assertEquals(offer.getPassengers(), 2);
    assertEquals(offer.getPorters(), 2);
    assertEquals(offer.getTimeout(), 20);
    assertEquals(offer.getRoutePoint(), routePoint);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(offer, new Offer(7, "com", 1200239, 7000, 2, 2, 20, routePoint));
    assertNotEquals(offer, new Offer(8, "com", 1200239, 7000, 2, 2, 20, routePoint));
    assertNotEquals(offer, new Offer(7, "co", 1200239, 7000, 2, 2, 20, routePoint));
    assertNotEquals(offer, new Offer(7, "com", 120023, 7000, 2, 2, 20, routePoint));
    assertNotEquals(offer, new Offer(7, "com", 1200239, 700, 2, 2, 20, routePoint));
    assertNotEquals(offer, new Offer(7, "com", 1200239, 7000, 3, 2, 20, routePoint));
    assertNotEquals(offer, new Offer(7, "com", 1200239, 7000, 2, 5, 20, routePoint));
    assertNotEquals(offer, new Offer(7, "com", 1200239, 7000, 2, 5, 21, routePoint));
    assertNotEquals(offer,
        new Offer(7, "com", 1200239, 7000, 2, 2, 20, new RoutePoint(10, 3, "com", "add"))
    );
  }
}