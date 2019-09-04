package com.cargopull.executor_driver.entity;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Неизменная бизнес сущность заказа. Содержит в себе ID, комментарий, расстояние до клиента,
 * предполагаемую цену, список опций и таймуат предложения. Список опций неизменен, но его
 * содержимое может изменяться - дополняться или заменяться. Список точек маршрута неизменен, но его
 * содержимое может изменяться - дополняться или заменяться.
 */
public class Order {

  private final long id;
  @NonNull
  private final PaymentType paymentType;
  @NonNull
  private final String comment;
  @NonNull
  private final String serviceName;
  private final int distance;
  @NonNull
  private final String estimatedPriceText;
  private final long estimatedPrice;
  private final long estimatedTime;
  private final long estimatedRouteLength;
  private final long totalCost;
  private final long timeout;
  private final long etaToStartPoint;
  private final long confirmationTime;
  private final long startTime;
  private final long scheduledStartTime;
  @NonNull
  private final RouteType routeType;
  @NonNull
  private final List<Option> options = new ArrayList<>();
  @NonNull
  private final List<RoutePoint> routePath = new ArrayList<>();

  public Order(long id, @NonNull PaymentType paymentType,
      @NonNull String comment, @NonNull String serviceName, int distance,
      @NonNull String estimatedPriceText, long estimatedPrice, long estimatedTime,
      long estimatedRouteLength, long totalCost, long timeout, long etaToStartPoint,
      long confirmationTime, long startTime, long scheduledStartTime,
      @NonNull RouteType routeType) {
    this.id = id;
    this.paymentType = paymentType;
    this.comment = comment;
    this.serviceName = serviceName;
    this.distance = distance;
    this.estimatedPriceText = estimatedPriceText;
    this.estimatedPrice = estimatedPrice;
    this.estimatedTime = estimatedTime;
    this.estimatedRouteLength = estimatedRouteLength;
    this.totalCost = totalCost;
    this.timeout = timeout;
    this.etaToStartPoint = etaToStartPoint;
    this.confirmationTime = confirmationTime;
    this.startTime = startTime;
    this.scheduledStartTime = scheduledStartTime;
    this.routeType = routeType;
  }

  // Возвращаем копию заказа с измененным ETA до первой точки.
  @NonNull
  public Order withEtaToStartPoint(long etaToStartPoint) {
    return new Order(id, paymentType, comment, serviceName, distance, estimatedPriceText, estimatedPrice,
        estimatedTime, estimatedRouteLength, totalCost, timeout, etaToStartPoint, confirmationTime,
        startTime, scheduledStartTime, routeType).setOptions(options).setRoutePoints(routePath);
  }

  public long getId() {
    return id;
  }

  @NonNull
  public PaymentType getPaymentType() {
    return paymentType;
  }

  @NonNull
  public String getComment() {
    return comment;
  }

  @NonNull
  public String getServiceName() {
    return serviceName;
  }

  public int getDistance() {
    return distance;
  }

  @NonNull
  public String getEstimatedPriceText() {
    return estimatedPriceText;
  }

  public long getEstimatedPrice() {
    return estimatedPrice;
  }

  public long getEstimatedTime() {
    return estimatedTime;
  }

  public long getEstimatedRouteLength() {
    return estimatedRouteLength;
  }

  public long getTotalCost() {
    return totalCost;
  }

  public long getTimeout() {
    return timeout;
  }

  public long getEtaToStartPoint() {
    return etaToStartPoint;
  }

  public long getConfirmationTime() {
    return confirmationTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getScheduledStartTime() {
    return scheduledStartTime;
  }

  @NonNull
  public RoutePoint getNextActiveRoutePoint() {
    for (RoutePoint routePoint : routePath) {
      if (routePoint.getRoutePointState() == RoutePointState.ACTIVE) {
        return routePoint;
      }
    }
    return routePath.get(0);
  }

  @NonNull
  public List<Option> getOptions() {
    return options;
  }

  private Order setOptions(@NonNull List<Option> options) {
    this.options.clear();
    this.options.addAll(options);
    return this;
  }

  public void setOptions(@NonNull Option... options) {
    this.options.clear();
    addOptions(options);
  }

  public void addOptions(@NonNull Option... options) {
    this.options.addAll(Arrays.asList(options));
  }

  @NonNull
  public RouteType getRouteType() {
    return routeType;
  }

  @NonNull
  public List<RoutePoint> getRoutePath() {
    return routePath;
  }

  void setRoutePoints(@NonNull RoutePoint... routePoints) {
    routePath.clear();
    addRoutePoints(routePoints);
  }

  public void addRoutePoints(@NonNull RoutePoint... routePoints) {
    routePath.addAll(Arrays.asList(routePoints));
  }

  private Order setRoutePoints(@NonNull List<RoutePoint> routePoints) {
    routePath.clear();
    routePath.addAll(routePoints);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Order order = (Order) o;

    return id == order.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
