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

  @Override
  public String toString() {
    return "RoutePoint{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", comment='" + comment + '\'' +
        ", address='" + address + '\'' +
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

    RoutePoint that = (RoutePoint) o;

    if (Double.compare(that.latitude, latitude) != 0) {
      return false;
    }
    if (Double.compare(that.longitude, longitude) != 0) {
      return false;
    }
    if (!comment.equals(that.comment)) {
      return false;
    }
    return address.equals(that.address);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + comment.hashCode();
    result = 31 * result + address.hashCode();
    return result;
  }
}
