package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersSummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.utils.Pair;
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
        successOrders == null ? new Pair<>(0, 0L) : new Pair<>(successOrders.getCount(),
            successOrders.getTotalAmount()),
        refusedOrders == null ? new Pair<>(0, 0L) : new Pair<>(refusedOrders.getCount(),
            refusedOrders.getTotalAmount()),
        cancelledOrders == null ? new Pair<>(0, 0L) : new Pair<>(cancelledOrders.getCount(),
            cancelledOrders.getTotalAmount()),
        skippedOrders == null ? new Pair<>(0, 0L) : new Pair<>(skippedOrders.getCount(),
            skippedOrders.getTotalAmount())
    );
  }
}
