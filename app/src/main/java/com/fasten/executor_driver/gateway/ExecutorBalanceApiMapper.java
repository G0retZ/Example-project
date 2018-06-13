package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.websocket.incoming.ApiExecutorBalance;
import com.fasten.executor_driver.entity.ExecutorBalance;
import com.google.gson.Gson;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorBalanceApiMapper implements Mapper<String, ExecutorBalance> {

  @Inject
  public ExecutorBalanceApiMapper() {
  }

  @NonNull
  @Override
  public ExecutorBalance map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiExecutorBalance apiExecutorBalance;
    try {
      apiExecutorBalance = gson.fromJson(from, ApiExecutorBalance.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON!", e);
    }
    return new ExecutorBalance(
        apiExecutorBalance.getMainAccount(),
        apiExecutorBalance.getBonusAccount(),
        apiExecutorBalance.getNonCashAccount()
    );
  }
}
