package com.fasten.executor_driver.presentation.geolocation;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации к решению проблемы с геолокацией.
 */
@StringDef({
    GeoLocationNavigate.RESOLVE_GEO_PROBLEM,
    GeoLocationNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface GeoLocationNavigate {

  // Переход к решению проблемы с геолокацией.
  String RESOLVE_GEO_PROBLEM = "GeoLocation.to.Resolve.GeoLocation.Problem";

  // Переход к ошибке соединения.
  String NO_CONNECTION = "GeoLocation.to.NoConnection";
}
