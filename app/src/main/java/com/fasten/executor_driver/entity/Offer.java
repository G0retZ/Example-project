package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность заказа. Содержит в себе ID, комментарий, расстояние до клиента,
 * предполагаемую цену, количество пассажиров/грузчиков и таймуат предложения.
 */
public class Offer {

  private final long id;
  @NonNull
  private final String comment;
  private final long distance;
  private final long estimatedPrice;
  private final int passengers;
  private final int porters;
  private final int timeout;
  @NonNull
  private final RoutePoint routePoint;

  public Offer(long id, @NonNull String comment, long distance, long estimatedPrice, int passengers,
      int porters, int timeout, @NonNull RoutePoint routePoint) {
    this.id = id;
    this.comment = comment;
    this.distance = distance;
    this.estimatedPrice = estimatedPrice;
    this.passengers = passengers;
    this.porters = porters;
    this.timeout = timeout;
    this.routePoint = routePoint;
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

  public long getEstimatedPrice() {
    return estimatedPrice;
  }

  public int getPassengers() {
    return passengers;
  }

  public int getPorters() {
    return porters;
  }

  public int getTimeout() {
    return timeout;
  }

  @NonNull
  public RoutePoint getRoutePoint() {
    return routePoint;
  }

  @Override
  public String toString() {
    return "Offer{" +
        "id=" + id +
        ", comment='" + comment + '\'' +
        ", distance=" + distance +
        ", estimatedPrice=" + estimatedPrice +
        ", passengers=" + passengers +
        ", porters=" + porters +
        ", timeout=" + timeout +
        ", routePoint=" + routePoint +
        '}';
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Offer offer = (Offer) o;

    if (id != offer.id) {
      return false;
    }
    if (distance != offer.distance) {
      return false;
    }
    if (estimatedPrice != offer.estimatedPrice) {
      return false;
    }
    if (passengers != offer.passengers) {
      return false;
    }
    if (porters != offer.porters) {
      return false;
    }
    if (timeout != offer.timeout) {
      return false;
    }
    if (!comment.equals(offer.comment)) {
      return false;
    }
    return routePoint.equals(offer.routePoint);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + comment.hashCode();
    result = 31 * result + (int) (distance ^ (distance >>> 32));
    result = 31 * result + (int) (estimatedPrice ^ (estimatedPrice >>> 32));
    result = 31 * result + passengers;
    result = 31 * result + porters;
    result = 31 * result + timeout;
    result = 31 * result + routePoint.hashCode();
    return result;
  }
}
