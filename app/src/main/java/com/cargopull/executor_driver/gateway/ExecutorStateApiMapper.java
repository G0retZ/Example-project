package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorStateApiMapper implements Mapper<StompMessage, ExecutorState> {

  @NonNull
  private final Mapper<StompMessage, String> payloadMapper;

  @Inject
  public ExecutorStateApiMapper(@NonNull Mapper<StompMessage, String> payloadMapper) {
    this.payloadMapper = payloadMapper;
  }

  @NonNull
  @Override
  public ExecutorState map(@NonNull StompMessage from) throws Exception {
    ExecutorState executorState;
    try {
      if ("true".equals(from.findHeader("Blocked"))) {
        executorState = ExecutorState.BLOCKED;
      } else {
        executorState = ExecutorState.valueOf(from.findHeader("Status").trim());
      }
      String customerTimer = from.findHeader("CustomerConfirmationTimer");
      if (customerTimer != null) {
        executorState.setCustomerTimer(Long.valueOf(customerTimer));
      } else {
        executorState.setCustomerTimer(0);
      }
      executorState.setData(payloadMapper.map(from));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат статуса!", e);
    }
    return executorState;
  }
}
