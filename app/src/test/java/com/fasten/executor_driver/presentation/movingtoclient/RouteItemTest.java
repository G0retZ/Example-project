package com.fasten.executor_driver.presentation.movingtoclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.utils.TimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RouteItemTest {

  private RouteItem routeItem;

  @Mock
  private Order order;
  @Mock
  private Order order2;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private TimeUtils timeUtils2;

  @Before
  public void setUp() {
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L, 12395182L, 12400182L);
    routeItem = new RouteItem(order, timeUtils);
  }

  @Test
  public void testGetters() {
    // Дано:
    when(order.getRoutePoint()).thenReturn(routePoint);
    when(order.getEtaToStartPoint()).thenReturn(358L);
    when(order.getConfirmationTime()).thenReturn(12384000L);
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getComment()).thenReturn("com");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(routeItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(routeItem.getCoordinatesString(), "5.421,10.2341");
    assertEquals(routeItem.getAddress(), "add\ncom");
    assertEquals(routeItem.getSecondsToMeetClient(), 352);
    assertEquals(routeItem.getSecondsToMeetClient(), 347);
    assertEquals(routeItem.getSecondsToMeetClient(), 342);
  }

  @Test
  public void testEquals() {
    assertEquals(routeItem, new RouteItem(order, timeUtils));
    assertEquals(routeItem, new RouteItem(order, timeUtils2));
    assertNotEquals(routeItem, new RouteItem(order2, timeUtils));
  }
}