package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.fasten.executor_driver.backend.websocket.incoming.ApiOrder;
import com.google.gson.Gson;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class CurrentCostPollingTimersApiMapper implements Mapper<String, Pair<Long, Long>> {

  @Inject
  public CurrentCostPollingTimersApiMapper() {
  }

  @NonNull
  @Override
  public Pair<Long, Long> map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOrder apiOrder;
    try {
      apiOrder = gson.fromJson(from, ApiOrder.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON!", e);
    }
    return new Pair<>(apiOrder.getOverPackageTimer(), apiOrder.getOverPackagePeriod());
  }
}
