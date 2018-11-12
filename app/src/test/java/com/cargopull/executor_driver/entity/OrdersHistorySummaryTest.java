package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrdersHistorySummaryTest {

  @Test
  public void testGetters() {
    // Дано:
    OrdersHistorySummary ordersHistorySummary = new OrdersHistorySummary(10, 21, 32, 43);

    // Результат:
    assertEquals(ordersHistorySummary.getCompletedOrders(), 10);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 32);
    assertEquals(ordersHistorySummary.getCompletedOrders(), 10);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 21);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 32);
    assertEquals(ordersHistorySummary.getMissedOrders(), 43);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 21);
    assertEquals(ordersHistorySummary.getMissedOrders(), 43);
  }
}