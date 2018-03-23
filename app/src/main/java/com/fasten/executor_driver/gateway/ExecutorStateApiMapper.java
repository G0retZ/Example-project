package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorStateApiMapper implements Mapper<String, ExecutorState> {

  @Inject
  public ExecutorStateApiMapper() {
  }

  @NonNull
  @Override
  public ExecutorState map(@NonNull String from) throws Exception {
    ExecutorState executorState;
    try {
      executorState = ExecutorState.valueOf(from.replace("\"", ""));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат статуса!", e);
    }
    return executorState;
  }
}
