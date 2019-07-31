package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RouteType;

/**
 * Преобразуем ID заказа из хедера ответа сервера в заказ.
 */
public class CancelledOrderApiMapper implements Mapper<StompFrame, Order> {

  @NonNull
  @Override
  public Order map(@NonNull StompFrame from) throws Exception {
    try {
      return new Order(
          Long.valueOf(from.getHeaders().get("PreliminaryCancelled")),
          PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, RouteType.POLYGON);
    } catch (Throwable t) {
      throw new DataMappingException(t);
    }
  }
}
