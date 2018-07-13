package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Извлекаем стоимость сверх пакета из ответа сервера.
 */
public class OrderCurrentCostApiMapper implements Mapper<StompMessage, Integer> {

  @Inject
  public OrderCurrentCostApiMapper() {
  }

  @NonNull
  @Override
  public Integer map(@NonNull StompMessage from) throws Exception {
    try {
      return Integer.valueOf(from.findHeader("TotalAmount").trim());
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат стоимости!", e);
    }
  }
}
