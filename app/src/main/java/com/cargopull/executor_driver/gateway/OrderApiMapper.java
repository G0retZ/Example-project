package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiOrder;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.google.gson.Gson;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class OrderApiMapper implements Mapper<String, Order> {

  @NonNull
  private final Mapper<ApiOptionItem, Option> apiOptionMapper;
  @NonNull
  private final Mapper<ApiRoutePoint, RoutePoint> routePointMapper;

  @Inject
  public OrderApiMapper(@NonNull Mapper<ApiOptionItem, Option> apiOptionMapper,
      @NonNull Mapper<ApiRoutePoint, RoutePoint> routePointMapper) {
    this.apiOptionMapper = apiOptionMapper;
    this.routePointMapper = routePointMapper;
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
    if (apiOrder.getApiOrderService() == null) {
      throw new DataMappingException("Ошибка маппинга: Услуга не должна быть null!");
    }
    if (apiOrder.getApiOrderService().getName() == null) {
      throw new DataMappingException("Ошибка маппинга: Имя услуги не должно быть null!");
    }
    if (apiOrder.getExecutorDistance() == null) {
      throw new DataMappingException("Ошибка маппинга: Дистанция не должна быть null!");
    }
    if (apiOrder.getEtaToStartPoint() == 0) {
      throw new DataMappingException("Ошибка маппинга: ETA должно быть больше 0!");
    }
    if (apiOrder.getRoute() == null) {
      throw new DataMappingException("Ошибка маппинга: маршрут не должен быть null!");
    }
    if (apiOrder.getRoute().isEmpty()) {
      throw new DataMappingException(
          "Ошибка маппинга: маршрут должен содержать хотя бы одну точку!"
      );
    }
    Order order = new Order(
        apiOrder.getId(),
        apiOrder.getComment() == null ? "" : apiOrder.getComment(),
        apiOrder.getApiOrderService().getName(),
        apiOrder.getExecutorDistance().getDistance(),
        apiOrder.getEstimatedAmountText() == null ? "" : apiOrder.getEstimatedAmountText(),
        apiOrder.getEstimatedAmount(),
        apiOrder.getEstimatedTime(),
        apiOrder.getEstimatedRouteDistance(),
        apiOrder.getTotalAmount(),
        apiOrder.getTimeout(),
        apiOrder.getEtaToStartPoint(),
        apiOrder.getConfirmationTime(),
        apiOrder.getOrderStartTime());
    if (apiOrder.getOptions() != null) {
      for (ApiOptionItem vehicleOptionItem : apiOrder.getOptions()) {
        order.addOptions(apiOptionMapper.map(vehicleOptionItem));
      }
    }
    for (ApiRoutePoint routePoint : apiOrder.getRoute()) {
      order.addRoutePoints(routePointMapper.map(routePoint));
    }
    return order;
  }
}
