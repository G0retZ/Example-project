package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import javax.inject.Inject;

/**
 * Извлекаем стоимость сверх пакета из ответа сервера.
 */
public class OrderCurrentCostApiMapper implements Mapper<StompFrame, Long> {

  @Inject
  public OrderCurrentCostApiMapper() {
  }

  @NonNull
  @Override
  public Long map(@NonNull StompFrame from) throws Exception {
    try {
      return Long.valueOf(from.getHeaders().get("TotalAmount"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат стоимости!", e);
    }
  }
}
