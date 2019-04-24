package com.cargopull.executor_driver.backend.web.incoming;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiRoutePoint {

  @SerializedName("index")
  private long index;
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
  @Nullable
  @SerializedName("status")
  private String status;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiRoutePoint() {
  }

  public ApiRoutePoint(long index, double latitude, double longitude, @Nullable String comment,
      @Nullable String address, @Nullable String status) {
    this.index = index;
    this.latitude = latitude;
    this.longitude = longitude;
    this.comment = comment;
    this.address = address;
    this.status = status;
  }

  public long getIndex() {
    return index;
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

  @Nullable
  public String getStatus() {
    return status;
  }
}
