package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность точки маршрута. Содержит в себе долготу, широту, адрес и комментарий.
 */
public class RoutePoint {

  private final long id;
  private final double latitude;
  private final double longitude;
  @NonNull
  private final String comment;
  @NonNull
  private final String address;
  private final boolean checked;

  public RoutePoint(long id, double latitude, double longitude, @NonNull String comment,
      @NonNull String address, boolean checked) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.comment = comment;
    this.address = address;
    this.checked = checked;
  }

  public long getId() {
    return id;
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

  public boolean isChecked() {
    return checked;
  }
}
