package com.fasten.executor_driver.entity;

/**
 * Неизменная бизнес геопозиции. Содержит в себе долготу, широту и временной штамп своего получения.
 */
public class GeoLocation {

  private final double latitude;
  private final double longitude;
  private final long timestamp;

  public GeoLocation(double latitude, double longitude, long timestamp) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.timestamp = timestamp;
  }

  double getLatitude() {
    return latitude;
  }

  double getLongitude() {
    return longitude;
  }

  long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "GeoLocation{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", timestamp=" + timestamp +
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

    GeoLocation that = (GeoLocation) o;

    if (Double.compare(that.latitude, latitude) != 0) {
      return false;
    }
    if (Double.compare(that.longitude, longitude) != 0) {
      return false;
    }
    return timestamp == that.timestamp;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    return result;
  }
}
