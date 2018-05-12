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
public class Offer {

  private final long id;
  @NonNull
  private final String comment;
  private final long distance;
  private final String estimatedPrice;
  private final int timeout;
  private final long eta;
  private final long timeStamp;
  @NonNull
  private final List<Option> options = new ArrayList<>();
  @NonNull
  private final RoutePoint routePoint;

  public Offer(long id, @NonNull String comment, long distance, String estimatedPrice, int timeout,
      long eta, long timeStamp, @NonNull RoutePoint routePoint) {
    this.id = id;
    this.comment = comment;
    this.distance = distance;
    this.estimatedPrice = estimatedPrice;
    this.timeout = timeout;
    this.routePoint = routePoint;
    this.eta = eta;
    this.timeStamp = timeStamp;
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

  public long getEta() {
    return eta;
  }

  public long getTimeStamp() {
    return timeStamp;
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
