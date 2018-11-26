package com.cargopull.executor_driver.entity;

/**
 * Неизменная бизнес сущность сводки истории заказов. Содержит в себе общие суммы заработка на
 * выполненных заказах и убытков от отказанных, отмененных и пропущенных заказов.
 */
public class OrdersHistorySummary {

  private final long completedOrders;
  private final long rejectedOrders;
  private final long cancelledOrders;
  private final long missedOrders;

  public OrdersHistorySummary(long completedOrders, long rejectedOrders, long cancelledOrders,
      long missedOrders) {
    this.completedOrders = completedOrders;
    this.rejectedOrders = rejectedOrders;
    this.cancelledOrders = cancelledOrders;
    this.missedOrders = missedOrders;
  }

  public long getCompletedOrders() {
    return completedOrders;
  }

  public long getRejectedOrders() {
    return rejectedOrders;
  }

  public long getCancelledOrders() {
    return cancelledOrders;
  }

  public long getMissedOrders() {
    return missedOrders;
  }
}
