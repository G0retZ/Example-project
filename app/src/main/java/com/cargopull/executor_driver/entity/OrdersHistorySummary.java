package com.cargopull.executor_driver.entity;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.utils.Pair;

/**
 * Неизменная бизнес сущность сводки истории заказов. Содержит в себе общие суммы заработка на
 * выполненных заказах и убытков от отказанных, отмененных и пропущенных заказов.
 */
public class OrdersHistorySummary {

  @NonNull
  private final Pair<Integer, Long> completedOrders;
  @NonNull
  private final Pair<Integer, Long> rejectedOrders;
  @NonNull
  private final Pair<Integer, Long> cancelledOrders;
  @NonNull
  private final Pair<Integer, Long> missedOrders;

  public OrdersHistorySummary(@NonNull Pair<Integer, Long> completedOrders,
      @NonNull Pair<Integer, Long> rejectedOrders, @NonNull Pair<Integer, Long> cancelledOrders,
      @NonNull Pair<Integer, Long> missedOrders) {
    this.completedOrders = completedOrders;
    this.rejectedOrders = rejectedOrders;
    this.cancelledOrders = cancelledOrders;
    this.missedOrders = missedOrders;
  }

  public int getCompletedOrdersCount() {
    return completedOrders.first;
  }

  public int getRejectedOrdersCount() {
    return rejectedOrders.first;
  }

  public int getCancelledOrdersCount() {
    return cancelledOrders.first;
  }

  public int getMissedOrdersCount() {
    return missedOrders.first;
  }

  public long getCompletedOrders() {
    return completedOrders.second;
  }

  public long getRejectedOrders() {
    return rejectedOrders.second;
  }

  public long getCancelledOrders() {
    return cancelledOrders.second;
  }

  public long getMissedOrders() {
    return missedOrders.second;
  }
}
