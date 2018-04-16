package com.fasten.executor_driver.presentation.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.RoutePoint;
import org.junit.Before;
import org.junit.Test;

public class OfferItemTest {

  private OfferItem offerItem;

  @Before
  public void setUp() {
    offerItem = new OfferItem(
        new Offer(7, "com", 1200239, 7000, 3, 2, 20, new RoutePoint(10.2341, 5.421, "com", "add"))
    );
  }

  @Test
  public void testGetters() {
    assertEquals(offerItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=288x352&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(offerItem.getPrice(), "от 7000 рублей");
    assertEquals(offerItem.getAddress(), "add");
    assertEquals(offerItem.getDistance(), 1200239);
    assertEquals(offerItem.getOfferComment(), "com");
    assertEquals(offerItem.getPortersCount(), 2);
    assertEquals(offerItem.getPassengersCount(), 3);
  }

  @Test
  public void testEquals() {
    assertEquals(offerItem, new OfferItem(new Offer(
        7, "com", 1200239, 7000, 3, 2, 20,
        new RoutePoint(10.2341, 5.421, "com", "add")
    )));
    assertNotEquals(offerItem, new OfferItem(new Offer(
        8, "com", 1200239, 7000, 3, 2, 20,
        new RoutePoint(10.2341, 5.421, "com", "add")
    )));
  }
}