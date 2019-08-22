package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.entity.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

/**
 * Преобразуем сообщение сервера в бизнес объект списка предзаказов.
 */
public class PreOrdersListApiMapper implements Mapper<StompFrame, Set<Order>> {

  @NonNull
  private final Mapper<ApiOrder, Order> apiOrderMapper;

  @Inject
  public PreOrdersListApiMapper(@NonNull Mapper<ApiOrder, Order> apiOrderMapper) {
    this.apiOrderMapper = apiOrderMapper;
  }

  @NonNull
  @Override
  public Set<Order> map(@NonNull StompFrame from) throws Exception {
    if (from.getBody() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getBody().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    Type type = new TypeToken<Set<ApiOrder>>() {
    }.getType();
    Set<ApiOrder> apiOrders;
    try {
      apiOrders = gson.fromJson(from.getBody(), type);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    Set<Order> orders = new HashSet<>();
    for (ApiOrder apiOrder : apiOrders) {
      orders.add(apiOrderMapper.map(apiOrder));
    }
    return orders;
  }
}
