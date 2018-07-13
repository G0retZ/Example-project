package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class GeoLocationTest {

  private GeoLocation geoLocation;

  @Before
  public void setUp() {
    geoLocation = new GeoLocation(12.1, 14.5, 12309);
  }

  @Test
  public void testConstructor() {
    assertEquals(geoLocation.getLatitude(), 12.1, 0);
    assertEquals(geoLocation.getLongitude(), 14.5, 0);
    assertEquals(geoLocation.getTimestamp(), 12309);
  }

  @Test
  public void testEquals() {
    assertEquals(geoLocation, new GeoLocation(12.1, 14.5, 12309));
    assertNotEquals(geoLocation, new GeoLocation(12.7, 14.5, 12309));
    assertNotEquals(geoLocation, new GeoLocation(12.1, 14.3, 12309));
    assertNotEquals(geoLocation, new GeoLocation(12.1, 14.5, 1209));
  }
}