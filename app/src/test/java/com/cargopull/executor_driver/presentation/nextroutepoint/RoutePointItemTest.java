package com.cargopull.executor_driver.presentation.nextroutepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.RoutePoint;

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

  @Before
  public void setUp() {
    routeItem = new RoutePointItem(routePoint);
  }

  @Test
  public void testGetters() {
    // Given:
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getComment()).thenReturn("com");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

      // Effect:
    assertEquals(routeItem.getCoordinatesString(), "5.421,10.2341");
    assertEquals(routeItem.getAddress(), "add");
    assertEquals(routeItem.getComment(), "com");
  }

  @Test
  public void testEquals() {
    assertEquals(routeItem, new RoutePointItem(routePoint));
    assertNotEquals(routeItem, new RoutePointItem(routePoint1));
  }
}