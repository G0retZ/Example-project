package com.fasten.executor_driver.presentation.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.RoutePoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OfferItemTest {

  private OfferItem offerItem;

  @Mock
  private Offer offer;
  @Mock
  private Offer offer2;

  @Mock
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    offerItem = new OfferItem(offer);
  }

  @Test
  public void testGetters() {
    // Дано:
    when(offer.getRoutePoint()).thenReturn(routePoint);
    when(offer.getDistance()).thenReturn(1200239L);
    when(offer.getComment()).thenReturn("com");
    when(offer.getEstimatedPrice()).thenReturn(7000L);
    when(offer.getPorters()).thenReturn(2);
    when(offer.getPassengers()).thenReturn(3);
    when(offer.getTimeout()).thenReturn(20);
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getLatitude()).thenReturn(10.2341);
    when(routePoint.getLongitude()).thenReturn(5.421);

    // Результат:
    assertEquals(offerItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=288x352&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(offerItem.getPrice(), "от 7000 рублей");
    assertEquals(offerItem.getAddress(), "add");
    assertEquals(offerItem.getDistance(), 1200239);
    assertEquals(offerItem.getOfferComment(), "com");
    assertEquals(offerItem.getPortersCount(), 2);
    assertEquals(offerItem.getPassengersCount(), 3);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertEquals(offerItem.getProgressLeft()[0] * 1d, 15000d, 100);
    assertEquals(offerItem.getProgressLeft()[1] * 1d, 75d, 1);
  }

  @Test
  public void testEquals() {
    assertEquals(offerItem, new OfferItem(offer));
    assertNotEquals(offerItem, new OfferItem(offer2));
  }
}