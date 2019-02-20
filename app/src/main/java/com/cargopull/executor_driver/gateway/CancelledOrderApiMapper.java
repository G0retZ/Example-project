package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем ID заказа из хедера ответа сервера в заказ.
 */
public class CancelledOrderApiMapper implements Mapper<StompMessage, Order> {

  @NonNull
  @Override
  public Order map(@NonNull StompMessage from) throws Exception {
    try {
      return new Order(
          Long.valueOf(from.findHeader("PreliminaryCancelled")),
          PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0
      );
    } catch (Throwable t) {
      throw new DataMappingException(t);
    }
  }
}
