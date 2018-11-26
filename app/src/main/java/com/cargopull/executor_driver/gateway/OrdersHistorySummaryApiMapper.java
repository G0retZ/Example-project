package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersHistorySummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import javax.inject.Inject;

/**
 * Преобразуем сводку истории заказов из ответа сервера в бизнес объект сводки истории заказов.
 */
public class OrdersHistorySummaryApiMapper implements
    Mapper<ApiOrdersHistorySummary, OrdersHistorySummary> {

  @Inject
  OrdersHistorySummaryApiMapper() {
  }

  @NonNull
  @Override
  public OrdersHistorySummary map(@NonNull ApiOrdersHistorySummary from) {
    return new OrdersHistorySummary(from.getSuccessOrders(), from.getRefusedOrders(),
        from.getCancelledOrders(), from.getSkippedOrders());
  }
}
