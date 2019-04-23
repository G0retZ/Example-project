package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class StompMapper<F, T> implements Mapper<StompMessage, T> {

  @NonNull
  private final Mapper<F, T> ftMapper;
  @NonNull
  private final Class<F> type;

  public StompMapper(@NonNull Mapper<F, T> ftMapper, @NonNull Class<F> type) {
    this.ftMapper = ftMapper;
    this.type = type;
  }

  @NonNull
  @Override
  public T map(@NonNull StompMessage from) throws Exception {
    if (from.getPayload() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getPayload().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    F from1;
    try {
      from1= gson.fromJson(from.getPayload(), type);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    return ftMapper.map(from1);
  }
}
