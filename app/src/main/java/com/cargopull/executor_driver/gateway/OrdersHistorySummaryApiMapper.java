package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersSummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import java.util.Map;
import javax.inject.Inject;

/**
 * Преобразуем сводку истории заказов из ответа сервера в бизнес объект сводки истории заказов.
 */
public class OrdersHistorySummaryApiMapper implements
    Mapper<Map<String, ApiOrdersSummary>, OrdersHistorySummary> {

  @Inject
  public OrdersHistorySummaryApiMapper() {
  }

  @NonNull
  @Override
  public OrdersHistorySummary map(@NonNull Map<String, ApiOrdersSummary> from) {
    ApiOrdersSummary successOrders = from.get("successOrders");
    ApiOrdersSummary refusedOrders = from.get("refusedOrders");
    ApiOrdersSummary cancelledOrders = from.get("cancelledOrders");
    ApiOrdersSummary skippedOrders = from.get("skippedOrders");
    return new OrdersHistorySummary(
        successOrders == null ? 0 : successOrders.getTotalAmount(),
        refusedOrders == null ? 0 : refusedOrders.getTotalAmount(),
        cancelledOrders == null ? 0 : cancelledOrders.getTotalAmount(),
        skippedOrders == null ? 0 : skippedOrders.getTotalAmount()
    );
  }
}
