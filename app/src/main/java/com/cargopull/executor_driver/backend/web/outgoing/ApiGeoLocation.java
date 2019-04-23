package com.cargopull.executor_driver.backend.web.outgoing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект геолокации для сериализации в JSON для отправки в сокет.
 */
public class ApiGeoLocation {

  @SerializedName("latitude")
  @Expose
  private final double latitude;
  @SerializedName("longitude")
  @Expose
  private final double longitude;
  @SerializedName("regDate")
  @Expose
  private final long regDate;

  public ApiGeoLocation(double latitude, double longitude, long regDate) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.regDate = regDate;
  }

  double getLatitude() {
    return latitude;
  }

  double getLongitude() {
    return longitude;
  }

  long getRegDate() {
    return regDate;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString() {
    return "ApiGeoLocation{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", regDate=" + regDate +
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

    ApiGeoLocation that = (ApiGeoLocation) o;

    if (Double.compare(that.latitude, latitude) != 0) {
      return false;
    }
    if (Double.compare(that.longitude, longitude) != 0) {
      return false;
    }
    return regDate == that.regDate;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (int) (regDate ^ (regDate >>> 32));
    return result;
  }
}
