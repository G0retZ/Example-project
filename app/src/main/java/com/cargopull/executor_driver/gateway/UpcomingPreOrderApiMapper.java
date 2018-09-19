package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import org.joda.time.format.DateTimeFormat;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем хедер и пэйлоад из ответа сервера в сокращенный бизнес объект заказа.
 */
public class UpcomingPreOrderApiMapper implements Mapper<StompMessage, Order> {

  @NonNull
  @Override
  public Order map(@NonNull StompMessage from) throws Exception {
    Long orderId;
    try {
      orderId = Long.valueOf(from.findHeader("OrderId"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат ИД!", e);
    }
    String eta = from.getPayload();
    if (eta == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    eta = eta.replaceAll("\\s+", "").replaceAll(".*([0-9][0-9]:[0-9][0-9]).*", "$1");
    if (eta.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: строка со временем не найдена!");
    }
    long etaToStartPoint;
    try {
      etaToStartPoint = DateTimeFormat.forPattern("HH:mm").parseLocalTime(eta).getMillisOfDay();
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат времени!", e);
    }
    return new Order(orderId, "", "", 0, "", 0, 0, 0, 0, 0, etaToStartPoint, 0, 0, 0);
  }
}
