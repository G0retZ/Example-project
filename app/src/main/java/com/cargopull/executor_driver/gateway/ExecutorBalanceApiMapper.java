package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.incoming.ApiExecutorBalance;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.google.gson.Gson;

import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorBalanceApiMapper implements Mapper<StompFrame, ExecutorBalance> {

  @Inject
  public ExecutorBalanceApiMapper() {
  }

  @NonNull
  @Override
  public ExecutorBalance map(@NonNull StompFrame from) throws Exception {
    if (from.getBody() == null) {
      throw new DataMappingException("Mapping error: data must not be null!");
    }
    if (from.getBody().trim().isEmpty()) {
      throw new DataMappingException("Mapping error: data must not be empty!");
    }
    Gson gson = new Gson();
    ApiExecutorBalance apiExecutorBalance;
    try {
      apiExecutorBalance = gson.fromJson(from.getBody(), ApiExecutorBalance.class);
    } catch (Exception e) {
      throw new DataMappingException("Mapping error: failed to parse JSON: " + from, e);
    }
    return new ExecutorBalance(
        apiExecutorBalance.getMainAccount(),
        apiExecutorBalance.getBonusAccount(),
        apiExecutorBalance.getNonCashAccount()
    );
  }
}
