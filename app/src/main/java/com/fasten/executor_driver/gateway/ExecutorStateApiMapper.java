package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class ExecutorStateApiMapper implements Mapper<StompMessage, ExecutorState> {

  @Inject
  public ExecutorStateApiMapper() {
  }

  @NonNull
  @Override
  public ExecutorState map(@NonNull StompMessage from) throws Exception {
    ExecutorState executorState;
    try {
      executorState = ExecutorState.valueOf(from.findHeader("Status").trim());
      executorState.setData(from.getPayload());
      String customerTimer = from.findHeader("CustomerConfirmationTimer");
      if (customerTimer != null) {
        executorState.setCustomerTimer(Long.valueOf(customerTimer));
      } else {
        executorState.setCustomerTimer(0);
      }
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат статуса!", e);
    }
    return executorState;
  }
}
