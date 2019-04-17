package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.entity.Order;
import com.google.gson.Gson;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class OrderStompMapper implements Mapper<StompMessage, Order> {

  @NonNull
  private final Mapper<ApiOrder, Order> apiOrderMapper;

  public OrderStompMapper(@NonNull Mapper<ApiOrder, Order> apiOrderMapper) {
    this.apiOrderMapper = apiOrderMapper;
  }

  @NonNull
  @Override
  public Order map(@NonNull StompMessage from) throws Exception {
    if (from.getPayload() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getPayload().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOrder apiOrder;
    try {
      apiOrder = gson.fromJson(from.getPayload(), ApiOrder.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    return apiOrderMapper.map(apiOrder);
  }
}
