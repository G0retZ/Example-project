package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiCancelOrderReason;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем причины отмены заказа из ответа сервера в бизнес объект статуса исполнителя.
 */
public class CancelOrderReasonApiMapper implements Mapper<StompMessage, List<CancelOrderReason>> {

  @Inject
  public CancelOrderReasonApiMapper() {
  }

  @NonNull
  @Override
  public List<CancelOrderReason> map(@NonNull StompMessage from) throws Exception {
    String fromString = from.getPayload().trim();
    if (fromString.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    Type type = new TypeToken<List<ApiCancelOrderReason>>() {
    }.getType();
    List<ApiCancelOrderReason> apiCancelOrderReasons;
    try {
      apiCancelOrderReasons = gson.fromJson(fromString, type);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    ArrayList<CancelOrderReason> cancelOrderReasons = new ArrayList<>();
    for (ApiCancelOrderReason apiCancelOrderReason : apiCancelOrderReasons) {
      if (apiCancelOrderReason.getDescription() == null) {
        throw new DataMappingException(
            "Ошибка маппинга: нет причины отказа для ИД = " + apiCancelOrderReason.getId() + " !");
      }
      cancelOrderReasons.add(new CancelOrderReason(apiCancelOrderReason.getId(),
          apiCancelOrderReason.getDescription(), apiCancelOrderReason.getName()));
    }
    return cancelOrderReasons;
  }
}
