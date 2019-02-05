package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiOrder;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем сообщение сервера в бизнес объект списка предзаказов.
 */
public class PreOrdersListApiMapper implements Mapper<StompMessage, Set<Order>> {

  @NonNull
  private final Mapper<ApiOptionItem, Option> apiOptionMapper;
  @NonNull
  private final Mapper<ApiRoutePoint, RoutePoint> routePointMapper;

  @Inject
  public PreOrdersListApiMapper(@NonNull Mapper<ApiOptionItem, Option> apiOptionMapper,
      @NonNull Mapper<ApiRoutePoint, RoutePoint> routePointMapper) {
    this.apiOptionMapper = apiOptionMapper;
    this.routePointMapper = routePointMapper;
  }

  @NonNull
  @Override
  public Set<Order> map(@NonNull StompMessage from) throws Exception {
    if (from.getPayload() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getPayload().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    Type type = new TypeToken<Set<ApiOrder>>() {
    }.getType();
    Set<ApiOrder> apiOrders;
    try {
      apiOrders = gson.fromJson(from.getPayload(), type);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    Set<Order> orders = new HashSet<>();
    for (ApiOrder apiOrder : apiOrders) {
      orders.add(map(apiOrder));
    }
    return orders;
  }

  private Order map(ApiOrder apiOrder) throws Exception {
    if (apiOrder.getPaymentType() == null) {
      throw new DataMappingException("Ошибка маппинга: Тип оплаты не должен быть null!");
    }
    PaymentType paymentType;
    try {
      paymentType = PaymentType.valueOf(apiOrder.getPaymentType());
    }     catch (Exception e) {
      throw new DataMappingException(
          "Ошибка маппинга: неизвестный способ оплаты \"" + apiOrder.getPaymentType() + "\" !");
    }
    if (apiOrder.getApiOrderService() == null) {
      throw new DataMappingException("Ошибка маппинга: Услуга не должна быть null!");
    }
    if (apiOrder.getApiOrderService().getName() == null) {
      throw new DataMappingException("Ошибка маппинга: Имя услуги не должно быть null!");
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
        paymentType,
        apiOrder.getComment() == null ? "" : apiOrder.getComment(),
        apiOrder.getApiOrderService().getName(),
        apiOrder.getExecutorDistance() == null ? 0 : apiOrder.getExecutorDistance().getDistance(),
        apiOrder.getEstimatedAmountText() == null ? "" : apiOrder.getEstimatedAmountText(),
        apiOrder.getEstimatedAmount(),
        apiOrder.getEstimatedTime(),
        apiOrder.getEstimatedRouteDistance(),
        apiOrder.getTotalAmount(),
        apiOrder.getTimeout(),
        apiOrder.getEtaToStartPoint(),
        apiOrder.getConfirmationTime(),
        apiOrder.getStartTime(),
        apiOrder.getScheduledStartTime());
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
