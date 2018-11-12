package com.cargopull.executor_driver.entity;

class OrdersHistorySummary {
  private final long completedOrders;
  private final long rejectedOrders;
  private final long cancelledOrders;
  private final long missedOrders;

  OrdersHistorySummary(long completedOrders, long rejectedOrders, long cancelledOrders,
      long missedOrders) {
    this.completedOrders = completedOrders;
    this.rejectedOrders = rejectedOrders;
    this.cancelledOrders = cancelledOrders;
    this.missedOrders = missedOrders;
  }

  long getCompletedOrders() {
    return completedOrders;
  }

  long getRejectedOrders() {
    return rejectedOrders;
  }

  long getCancelledOrders() {
    return cancelledOrders;
  }

  long getMissedOrders() {
    return missedOrders;
  }
}
