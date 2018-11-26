package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import io.reactivex.Single;

/**
 * Гейтвей сводки истории заказов.
 */
public interface OrdersHistorySummaryGateway {

  /**
   * Запрашивает сводку истории заказов.
   *
   * @param fromDate Дата начала периода, включительно
   * @param toDate Дата конца периода, включительно
   * @return {@link Single<OrdersHistorySummary>} сводка истории заказов
   */
  @NonNull
  Single<OrdersHistorySummary> getOrdersHistorySummary(long fromDate, long toDate);
}
