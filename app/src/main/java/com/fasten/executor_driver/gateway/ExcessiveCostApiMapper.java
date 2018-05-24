package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Извлекаем стоимость сверх пакета из ответа сервера.
 */
class ExcessiveCostApiMapper implements Mapper<StompMessage, Integer> {

  @Inject
  ExcessiveCostApiMapper() {
  }

  @NonNull
  @Override
  public Integer map(@NonNull StompMessage from) throws Exception {
    try {
      return Integer.valueOf(from.getPayload().trim());
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат статуса!", e);
    }
  }
}
