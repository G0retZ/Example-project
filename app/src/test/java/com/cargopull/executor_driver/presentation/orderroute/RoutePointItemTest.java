package com.cargopull.executor_driver.presentation.orderroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoutePointItemTest {

  private RoutePointItem routePointItem;

  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;

  @Before
  public void setUp() {
    routePointItem = new RoutePointItem(routePoint);
  }

  @Test
  public void testProcessedGetters() {
    // Given:
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

      // Effect:
    assertEquals(routePointItem.getAddress(), "add");
    assertEquals(routePointItem.getRoutePoint(), routePoint);
    assertTrue(routePointItem.isProcessed());
    assertFalse(routePointItem.isActive());
  }

  @Test
  public void testActiveGetters() {
      // Given:
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);

      // Effect:
    assertEquals(routePointItem.getAddress(), "add");
    assertEquals(routePointItem.getRoutePoint(), routePoint);
    assertFalse(routePointItem.isProcessed());
    assertTrue(routePointItem.isActive());
  }

  @Test
  public void testQueuedGetters() {
      // Given:
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);

      // Effect:
    assertEquals(routePointItem.getAddress(), "add");
    assertEquals(routePointItem.getRoutePoint(), routePoint);
    assertFalse(routePointItem.isProcessed());
    assertFalse(routePointItem.isActive());
  }

  @Test
  public void testEquals() {
    assertEquals(routePointItem, new RoutePointItem(routePoint));
    assertNotEquals(routePointItem, new RoutePointItem(routePoint1));
  }
}