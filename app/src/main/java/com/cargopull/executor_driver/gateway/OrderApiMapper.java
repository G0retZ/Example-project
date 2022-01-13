package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RouteType;

import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class OrderApiMapper implements Mapper<ApiOrder, Order> {

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
  public Order map(@NonNull ApiOrder from) throws Exception {
    if (from.getPaymentType() == null) {
      throw new DataMappingException("Mapping error: Payment method must not be null!");
    }
    PaymentType paymentType;
    try {
      paymentType = PaymentType.valueOf(from.getPaymentType());
    } catch (Exception e) {
      throw new DataMappingException(
              "Mapping error: Unknown payment method \"" + from.getPaymentType() + "\" !");
    }
    RouteType routeType;
    try {
      routeType = RouteType.valueOf(from.getRouteType());
    } catch (Exception e) {
      throw new DataMappingException(
              "Mapping error: unknown route type \"" + from.getRouteType() + "\" !");
    }
    if (from.getApiOrderService() == null) {
      throw new DataMappingException("Mapping error: Service must not be null!");
    }
    if (from.getApiOrderService().getName() == null) {
      throw new DataMappingException("Mapping error: Service name must not be null!");
    }
    if (from.getRoute() == null) {
      throw new DataMappingException("Mapping error: route must not be null!");
    }
    if (from.getRoute().isEmpty()) {
      throw new DataMappingException(
              "Mapping error: route must have at least 1 point!"
      );
    }
    Order order = new Order(
        from.getId(),
        paymentType,
        from.getComment() == null ? "" : from.getComment(),
        from.getApiOrderService().getName(),
        from.getExecutorDistance() == null ? 0 : from.getExecutorDistance().getDistance(),
        from.getEstimatedAmountText() == null ? "" : from.getEstimatedAmountText(),
        from.getEstimatedAmount(),
        from.getEstimatedTime(),
        from.getEstimatedRouteDistance(),
        from.getTotalAmount(),
        from.getTimeout(),
        from.getEtaToStartPoint(),
        from.getConfirmationTime(),
        from.getStartTime(),
        from.getScheduledStartTime(),
        routeType);
    if (from.getOptions() != null) {
      for (ApiOptionItem vehicleOptionItem : from.getOptions()) {
        order.addOptions(apiOptionMapper.map(vehicleOptionItem));
      }
    }
    for (ApiRoutePoint routePoint : from.getRoute()) {
      order.addRoutePoints(routePointMapper.map(routePoint));
    }
    return order;
  }
}
