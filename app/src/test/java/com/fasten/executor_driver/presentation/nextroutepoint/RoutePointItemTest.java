package com.fasten.executor_driver.presentation.nextroutepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.RoutePoint;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoutePointItemTest {

  private RoutePointItem routeItem;

  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;
  @Mock
  private RoutePoint routePoint3;
  @Mock
  private RoutePoint routePoint4;

  @Before
  public void setUp() {
    when(routePoint.isChecked()).thenReturn(true);
    routeItem = new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2));
  }

  @Test
  public void testGetters() {
    // Дано:
    when(routePoint1.getAddress()).thenReturn("add");
    when(routePoint1.getComment()).thenReturn("com");
    when(routePoint1.getLatitude()).thenReturn(5.421);
    when(routePoint1.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(routeItem.getMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI&center=5.421,10.2341&maptype=roadmap&zoom=16&size=360x200");
    assertEquals(routeItem.getCoordinatesString(), "5.421,10.2341");
    assertEquals(routeItem.getAddress(), "add");
    assertEquals(routeItem.getComment(), "com");
  }

  @Test
  public void testEquals() {
    assertEquals(routeItem,
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2)));
    assertNotEquals(routeItem,
        new RoutePointItem(Arrays.asList(routePoint3, routePoint1, routePoint4)));
  }
}