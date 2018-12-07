package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.utils.Pair;
import org.junit.Test;

public class OrdersHistorySummaryTest {

  @Test
  public void testGetters() {
    // Дано:
    OrdersHistorySummary ordersHistorySummary = new OrdersHistorySummary(
        new Pair<>(2, 10L), new Pair<>(3, 21L), new Pair<>(4, 32L), new Pair<>(5, 43L)
    );

    // Результат:
    assertEquals(ordersHistorySummary.getCompletedOrders(), 10L);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 32L);
    assertEquals(ordersHistorySummary.getCompletedOrders(), 10L);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 21L);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 32);
    assertEquals(ordersHistorySummary.getMissedOrders(), 43);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 21);
    assertEquals(ordersHistorySummary.getMissedOrders(), 43);
    assertEquals(ordersHistorySummary.getCompletedOrdersCount(), 2);
    assertEquals(ordersHistorySummary.getCancelledOrdersCount(), 4);
    assertEquals(ordersHistorySummary.getCompletedOrdersCount(), 2);
    assertEquals(ordersHistorySummary.getRejectedOrdersCount(), 3);
    assertEquals(ordersHistorySummary.getCancelledOrdersCount(), 4);
    assertEquals(ordersHistorySummary.getMissedOrdersCount(), 5);
    assertEquals(ordersHistorySummary.getRejectedOrdersCount(), 3);
    assertEquals(ordersHistorySummary.getMissedOrdersCount(), 5);
  }
}