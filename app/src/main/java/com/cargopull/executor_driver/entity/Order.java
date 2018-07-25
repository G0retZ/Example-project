package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Неизменная бизнес сущность заказа. Содержит в себе ID, комментарий, расстояние до клиента,
 * предполагаемую цену, список опций и таймуат предложения.
 * Список опций неизменен, но его содержимое может изменяться - дополняться или заменяться.
 * Список точек маршрута неизменен, но его содержимое может изменяться - дополняться или заменяться.
 */
public class Order {

  private final long id;
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
  private final long orderStartTime;
  @NonNull
  private final List<Option> options = new ArrayList<>();
  @NonNull
  private final List<RoutePoint> routePath = new ArrayList<>();

  public Order(long id, @NonNull String comment, @NonNull String serviceName, int distance,
      @NonNull String estimatedPriceText, long estimatedPrice, long estimatedTime,
      long estimatedRouteLength, long totalCost, long timeout, long etaToStartPoint,
      long confirmationTime, long orderStartTime) {
    this.id = id;
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
    this.orderStartTime = orderStartTime;
  }

  public long getId() {
    return id;
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

  public long getOrderStartTime() {
    return orderStartTime;
  }

  @NonNull
  public List<Option> getOptions() {
    return options;
  }

  public void setOptions(@NonNull Option... options) {
    this.options.clear();
    addOptions(options);
  }

  public void addOptions(@NonNull Option... options) {
    this.options.addAll(Arrays.asList(options));
  }

  @NonNull
  public List<RoutePoint> getRoutePath() {
    return routePath;
  }

  public void setRoutePoints(@NonNull RoutePoint... routePoints) {
    routePath.clear();
    addRoutePoints(routePoints);
  }

  public void addRoutePoints(@NonNull RoutePoint... routePoints) {
    routePath.addAll(Arrays.asList(routePoints));
  }
}
