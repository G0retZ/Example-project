package com.cargopull.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiOrdersHistorySummaryTest {

  private ApiOrdersHistorySummary apiOrdersHistorySummary;

  @Before
  public void setUp() {
    apiOrdersHistorySummary = new ApiOrdersHistorySummary(10, 21, 32,43);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOrdersHistorySummary.getSuccessOrders(), 10);
    assertEquals(apiOrdersHistorySummary.getRefusedOrders(), 21);
    assertEquals(apiOrdersHistorySummary.getSkippedOrders(), 32);
    assertEquals(apiOrdersHistorySummary.getCancelledOrders(), 43);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(apiOrdersHistorySummary, new ApiOrdersHistorySummary(10, 21, 32,43));
    assertNotEquals(apiOrdersHistorySummary, new ApiOrdersHistorySummary(1, 21, 32,43));
    assertNotEquals(apiOrdersHistorySummary, new ApiOrdersHistorySummary(10, 2, 32,43));
    assertNotEquals(apiOrdersHistorySummary, new ApiOrdersHistorySummary(10, 21, 3,43));
    assertNotEquals(apiOrdersHistorySummary, new ApiOrdersHistorySummary(10, 21, 32,4));
    assertNotEquals(apiOrdersHistorySummary, null);
    assertNotEquals(apiOrdersHistorySummary, "");
  }
}