package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderTimers;
import com.cargopull.executor_driver.utils.Pair;
import com.google.gson.Gson;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class CurrentCostPollingTimersApiMapper implements Mapper<StompFrame, Pair<Long, Long>> {

  @Inject
  public CurrentCostPollingTimersApiMapper() {
  }

  @NonNull
  @Override
  public Pair<Long, Long> map(@NonNull StompFrame from) throws Exception {
    if (from.getBody() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getBody().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOrderTimers apiOrderTimers;
    try {
      apiOrderTimers = gson.fromJson(from.getBody(), ApiOrderTimers.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    long timer = 0;
    if (apiOrderTimers.getOverPackageTimer() != null) {
      timer = apiOrderTimers.getOverPackageTimer();
    }
    if (timer < 0) {
      throw new DataMappingException("Ошибка маппинга: время до поллинга должно быть больше 0!");
    }
    if (apiOrderTimers.getOverPackagePeriod() == null) {
      throw new DataMappingException("Ошибка маппинга: период поллинга не должен быть null!");
    }
    long period = apiOrderTimers.getOverPackagePeriod();
    if (period < 15_000) {
      throw new DataMappingException("Ошибка маппинга: период поллинга должен быть больше 15 сек!");
    }
    return new Pair<>(timer, period);
  }
}
