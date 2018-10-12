package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiExecutorBalance;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.google.gson.Gson;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorBalanceApiMapper implements Mapper<StompMessage, ExecutorBalance> {

  @Inject
  public ExecutorBalanceApiMapper() {
  }

  @NonNull
  @Override
  public ExecutorBalance map(@NonNull StompMessage from) throws Exception {
    if (from.getPayload() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getPayload().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiExecutorBalance apiExecutorBalance;
    try {
      apiExecutorBalance = gson.fromJson(from.getPayload(), ApiExecutorBalance.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    return new ExecutorBalance(
        apiExecutorBalance.getMainAccount(),
        apiExecutorBalance.getBonusAccount(),
        apiExecutorBalance.getNonCashAccount()
    );
  }
}
