package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Извлекаем стоимость сверх пакета из ответа сервера.
 */
public class OrderCurrentCostApiMapper implements Mapper<StompMessage, Long> {

  @Inject
  public OrderCurrentCostApiMapper() {
  }

  @NonNull
  @Override
  public Long map(@NonNull StompMessage from) throws Exception {
    try {
      return Long.valueOf(from.findHeader("TotalAmount"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат стоимости!", e);
    }
  }
}
