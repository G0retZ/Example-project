package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность точки маршрута. Содержит в себе долготу, широту, адрес и комментарий.
 */
public class RoutePoint {

  private final double latitude;
  private final double longitude;
  @NonNull
  private final String comment;
  @NonNull
  private final String address;

  public RoutePoint(double latitude, double longitude, @NonNull String comment,
      @NonNull String address) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.comment = comment;
    this.address = address;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @NonNull
  public String getComment() {
    return comment;
  }

  @NonNull
  public String getAddress() {
    return address;
  }
}
