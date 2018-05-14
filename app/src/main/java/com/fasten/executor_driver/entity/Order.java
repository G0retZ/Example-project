package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Неизменная бизнес сущность заказа. Содержит в себе ID, комментарий, расстояние до клиента,
 * предполагаемую цену, список опций и таймуат предложения.
 * Список опций неизменен, но его содержимое может изменяться - дополняться или заменяться.
 */
public class Order {

  private final long id;
  @NonNull
  private final String comment;
  private final long distance;
  private final String estimatedPrice;
  private final int timeout;
  private final long etaToStartPoint;
  private final long confirmationTime;
  @NonNull
  private final List<Option> options = new ArrayList<>();
  @NonNull
  private final RoutePoint routePoint;

  public Order(long id, @NonNull String comment, long distance, String estimatedPrice, int timeout,
      long etaToStartPoint, long confirmationTime, @NonNull RoutePoint routePoint) {
    this.id = id;
    this.comment = comment;
    this.distance = distance;
    this.estimatedPrice = estimatedPrice;
    this.timeout = timeout;
    this.routePoint = routePoint;
    this.etaToStartPoint = etaToStartPoint;
    this.confirmationTime = confirmationTime;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getComment() {
    return comment;
  }

  public long getDistance() {
    return distance;
  }

  public String getEstimatedPrice() {
    return estimatedPrice;
  }

  public int getTimeout() {
    return timeout;
  }

  public long getEtaToStartPoint() {
    return etaToStartPoint;
  }

  public long getConfirmationTime() {
    return confirmationTime;
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
  public RoutePoint getRoutePoint() {
    return routePoint;
  }
}
