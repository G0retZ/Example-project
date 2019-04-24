package com.cargopull.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiDriverDistancePairTest {

  private ApiDriverDistancePair apiDriverDistancePair;

  @Before
  public void setUp() {
    apiDriverDistancePair = new ApiDriverDistancePair(12345);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiDriverDistancePair.getDistance(), 12345);
  }
}