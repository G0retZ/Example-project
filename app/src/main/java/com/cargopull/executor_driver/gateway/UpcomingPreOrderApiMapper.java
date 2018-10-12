package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем хедер и пэйлоад из ответа сервера в сокращенный бизнес объект заказа.
 */
public class UpcomingPreOrderApiMapper implements Mapper<StompMessage, Order> {

  @NonNull
  @Override
  public Order map(@NonNull StompMessage from) throws Exception {
    long orderId;
    try {
      orderId = Long.valueOf(from.findHeader("OrderId"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат ИД!", e);
    }
    long etaToStartPoint;
    try {
      etaToStartPoint = Long.valueOf(from.findHeader("ETA"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат ETA!", e);
    }
    return new Order(orderId, "", "", 0, "", 0, 0, 0, 0, 0, etaToStartPoint, 0, 0, 0);
  }
}
