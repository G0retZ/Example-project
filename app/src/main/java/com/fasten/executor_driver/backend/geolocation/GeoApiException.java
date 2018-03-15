package com.fasten.executor_driver.backend.geolocation;

import android.app.PendingIntent;
import android.support.annotation.NonNull;

/**
 * Исключение о проблеме с геолокацией.
 */

public class GeoApiException extends Exception {

  @NonNull
  private final PendingIntent pendingIntent;

  GeoApiException(@NonNull PendingIntent pendingIntent) {
    super();
    this.pendingIntent = pendingIntent;
  }

  @NonNull
  public PendingIntent getPendingIntent() {
    return pendingIntent;
  }
}
