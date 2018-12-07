package com.cargopull.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiOrdersSummaryTest {

  private ApiOrdersSummary apiOrdersSummary;

  @Before
  public void setUp() {
    apiOrdersSummary = new ApiOrdersSummary(10, 21);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOrdersSummary.getCount(), 10);
    assertEquals(apiOrdersSummary.getTotalAmount(), 21);
  }
}