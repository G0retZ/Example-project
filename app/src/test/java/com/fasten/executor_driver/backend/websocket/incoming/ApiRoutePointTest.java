package com.fasten.executor_driver.backend.websocket.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ApiRoutePointTest {

  private ApiRoutePoint apiRoutePoint;

  @Before
  public void setUp() {
    apiRoutePoint = new ApiRoutePoint(4, 234, 567, "comment", "address", true);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiRoutePoint.getId(), 4);
    assertEquals(apiRoutePoint.getLatitude(), 234, Double.MIN_VALUE);
    assertEquals(apiRoutePoint.getLongitude(), 567, Double.MIN_VALUE);
    assertEquals(apiRoutePoint.getComment(), "comment");
    assertEquals(apiRoutePoint.getAddress(), "address");
    assertTrue(apiRoutePoint.isChecked());
  }
}