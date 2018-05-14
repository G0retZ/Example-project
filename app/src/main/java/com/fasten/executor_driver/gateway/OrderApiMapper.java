package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.backend.websocket.incoming.ApiOrder;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
import com.google.gson.Gson;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class OrderApiMapper implements Mapper<String, Order> {

  private final Mapper<ApiOptionItem, Option> apiOptionMapper;

  @Inject
  public OrderApiMapper(Mapper<ApiOptionItem, Option> apiOptionMapper) {
    this.apiOptionMapper = apiOptionMapper;
  }

  @NonNull
  @Override
  public Order map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOrder apiOrder;
    try {
      apiOrder = gson.fromJson(from, ApiOrder.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON!", e);
    }
    if (apiOrder.getRoute() == null) {
      throw new DataMappingException("Ошибка маппинга: маршрут не должен быть null!");
    }
    if (apiOrder.getRoute().isEmpty()) {
      throw new DataMappingException(
          "Ошибка маппинга: маршрут должен содержать хотя бы одну точку!"
      );
    }
    String address = apiOrder.getRoute().get(0).getAddress();
    if (address == null) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть null!");
    }
    if (address.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть пустым!");
    }
    if (apiOrder.getExecutorDistance() == null) {
      throw new DataMappingException("Ошибка маппинга: Дистанция не должна быть null!");
    }
    String comment = apiOrder.getRoute().get(0).getComment();
    Order order = new Order(
        apiOrder.getId(),
        apiOrder.getComment() == null ? "" : apiOrder.getComment(),
        apiOrder.getExecutorDistance().getDistance(),
        apiOrder.getEstimatedAmount(),
        // TODO: это костыль, который подменяет таймаут 0 на 20
        apiOrder.getTimeout() == 0 ? 20 : apiOrder.getTimeout(),
        apiOrder.getEtaToStartPoint(),
        apiOrder.getConfirmationTime(),
        new RoutePoint(
            apiOrder.getRoute().get(0).getLatitude(),
            apiOrder.getRoute().get(0).getLongitude(),
            comment == null ? "" : comment,
            address
        )
    );
    if (apiOrder.getOptions() != null && !apiOrder.getOptions().isEmpty()) {
      for (ApiOptionItem vehicleOptionItem : apiOrder.getOptions()) {
        order.addOptions(apiOptionMapper.map(vehicleOptionItem));
      }
    }
    return order;
  }
}
