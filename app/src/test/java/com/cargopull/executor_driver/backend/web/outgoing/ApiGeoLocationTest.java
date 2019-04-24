package com.cargopull.executor_driver.backend.web.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiGeoLocationTest {

  private ApiGeoLocation apiGeoLocation;

  @Before
  public void setUp() {
    apiGeoLocation = new ApiGeoLocation(1, 2, 3);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiGeoLocation.getLatitude(), 1, Double.MIN_VALUE);
    assertEquals(apiGeoLocation.getLongitude(), 2, Double.MIN_VALUE);
    assertEquals(apiGeoLocation.getRegDate(), 3);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(apiGeoLocation, new ApiGeoLocation(1, 2, 3));
    assertNotEquals(apiGeoLocation, new ApiGeoLocation(0, 2, 3));
    assertNotEquals(apiGeoLocation, new ApiGeoLocation(1, 1, 3));
    assertNotEquals(apiGeoLocation, new ApiGeoLocation(1, 2, 2));
  }
}