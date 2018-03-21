package com.fasten.executor_driver.backend.websocket.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiGeoLocationTest {

  private ApiGeoLocation apiGeoLocation;

  @Before
  public void setUp() throws Exception {
    apiGeoLocation = new ApiGeoLocation(1, 2, 3);
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiGeoLocation.getLatitude(), 1, Double.MIN_VALUE);
    assertEquals(apiGeoLocation.getLongitude(), 2, Double.MIN_VALUE);
    assertEquals(apiGeoLocation.getRegDate(), 3);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiGeoLocation, new ApiGeoLocation(1, 2, 3));
    assertNotEquals(apiGeoLocation, new ApiGeoLocation(0, 2, 3));
    assertNotEquals(apiGeoLocation, new ApiGeoLocation(1, 1, 3));
    assertNotEquals(apiGeoLocation, new ApiGeoLocation(1, 2, 2));
  }
}