package com.fasten.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiRoutePoint {

  @SerializedName("latitude")
  private double latitude;
  @SerializedName("longitude")
  private double longitude;
  @Nullable
  @SerializedName("comment")
  private String comment;
  @Nullable
  @SerializedName("address")
  private String address;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiRoutePoint() {
  }

  ApiRoutePoint(double latitude, double longitude, @Nullable String comment,
      @Nullable String address) {
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

  @Nullable
  public String getComment() {
    return comment;
  }

  @Nullable
  public String getAddress() {
    return address;
  }
}
