package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.websocket.incoming.ApiCancelOrderReason;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
class CancelOrderReasonApiMapper implements Mapper<String, List<CancelOrderReason>> {

  @Inject
  CancelOrderReasonApiMapper() {
  }

  @NonNull
  @Override
  public List<CancelOrderReason> map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    Type type = new TypeToken<List<ApiCancelOrderReason>>() {
    }.getType();
    List<ApiCancelOrderReason> apiCancelOrderReasons;
    try {
      apiCancelOrderReasons = gson.fromJson(from, type);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON!", e);
    }
    ArrayList<CancelOrderReason> cancelOrderReasons = new ArrayList<>();
    for (ApiCancelOrderReason apiCancelOrderReason : apiCancelOrderReasons) {
      if (apiCancelOrderReason.getDescription() == null) {
        throw new DataMappingException(
            "Ошибка маппинга: нет причины отказа для ИД = " + apiCancelOrderReason.getId() + " !");
      }
      cancelOrderReasons.add(new CancelOrderReason(apiCancelOrderReason.getId(),
          apiCancelOrderReason.getDescription()));
    }
    return cancelOrderReasons;
  }
}
