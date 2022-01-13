package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RouteType;

/**
 * Преобразуем хедер и пэйлоад из ответа сервера в сокращенный бизнес объект заказа.
 */
public class UpcomingPreOrderApiMapper implements Mapper<StompFrame, Order> {

  @NonNull
  @Override
  public Order map(@NonNull StompFrame from) throws Exception {
    long orderId;
    try {
      orderId = Long.parseLong(from.getHeaders().get("OrderId"));
    } catch (Exception e) {
      throw new DataMappingException("Mapping error: wrong format of ID!", e);
    }
    long etaToStartPoint;
    try {
      etaToStartPoint = Long.parseLong(from.getHeaders().get("ETA"));
    } catch (Exception e) {
      throw new DataMappingException("Mapping error: wrong format of ETA!", e);
    }
    return new Order(orderId, PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, etaToStartPoint, 0, 0,
        0, RouteType.POLYGON);
  }
}
