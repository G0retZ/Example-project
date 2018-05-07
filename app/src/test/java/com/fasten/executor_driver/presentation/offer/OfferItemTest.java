package com.fasten.executor_driver.presentation.offer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.utils.TimeUtils;
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
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private TimeUtils timeUtils2;

  @Before
  public void setUp() {
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L, 12395182L, 12400182L);
    offerItem = new OfferItem(offer, timeUtils);
  }

  @Test
  public void testGetters() {
    // Дано:
    when(offer.getRoutePoint()).thenReturn(routePoint);
    when(offer.getDistance()).thenReturn(1200239L);
    when(offer.getComment()).thenReturn("com");
    when(offer.getEstimatedPrice()).thenReturn("7000");
    when(offer.getTimeout()).thenReturn(20);
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(offerItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=288x352&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(offerItem.getPrice(), "7000");
    assertEquals(offerItem.getAddress(), "add");
    assertEquals(offerItem.getDistance(), 1200239);
    assertEquals(offerItem.getOfferComment(), "com");
    assertEquals(offerItem.getPortersCount(), 0);
    assertEquals(offerItem.getPassengersCount(), 0);
    assertArrayEquals(offerItem.getProgressLeft(), new long[]{75, 15000});
    assertArrayEquals(offerItem.getProgressLeft(), new long[]{50, 10000});
  }

  @Test
  public void testEquals() {
    assertEquals(offerItem, new OfferItem(offer, timeUtils));
    assertEquals(offerItem, new OfferItem(offer, timeUtils2));
    assertNotEquals(offerItem, new OfferItem(offer2, timeUtils));
  }
}