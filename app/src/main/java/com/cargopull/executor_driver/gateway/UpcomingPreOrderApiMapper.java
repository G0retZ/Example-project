package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;

/**
 * Преобразуем хедер и пэйлоад из ответа сервера в сокращенный бизнес объект заказа.
 */
public class UpcomingPreOrderApiMapper implements Mapper<StompFrame, Order> {

  @NonNull
  @Override
  public Order map(@NonNull StompFrame from) throws Exception {
    long orderId;
    try {
      orderId = Long.valueOf(from.getHeaders().get("OrderId"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат ИД!", e);
    }
    long etaToStartPoint;
    try {
      etaToStartPoint = Long.valueOf(from.getHeaders().get("ETA"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат ETA!", e);
    }
    return new Order(orderId, PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, etaToStartPoint, 0, 0, 0);
  }
}
