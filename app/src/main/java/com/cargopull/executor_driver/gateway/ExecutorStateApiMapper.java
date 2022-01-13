package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.ExecutorState;

import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorStateApiMapper implements Mapper<StompFrame, ExecutorState> {

  @NonNull
  private final Mapper<StompFrame, String> payloadMapper;

  @Inject
  public ExecutorStateApiMapper(@NonNull Mapper<StompFrame, String> payloadMapper) {
    this.payloadMapper = payloadMapper;
  }

  @NonNull
  @Override
  public ExecutorState map(@NonNull StompFrame from) throws Exception {
    ExecutorState executorState;
    try {
      if ("true".equals(from.getHeaders().get("Blocked"))) {
        executorState = ExecutorState.BLOCKED;
      } else {
        executorState = ExecutorState.valueOf(from.getHeaders().get("Status").trim());
      }
      String customerTimer = from.getHeaders().get("CustomerConfirmationTimer");
      if (customerTimer != null) {
        executorState.setCustomerTimer(Long.valueOf(customerTimer));
      } else {
        executorState.setCustomerTimer(0);
      }
      executorState.setData(payloadMapper.map(from));
    } catch (Exception e) {
      throw new DataMappingException("Mapping error: wrong format of status!", e);
    }
    return executorState;
  }
}
