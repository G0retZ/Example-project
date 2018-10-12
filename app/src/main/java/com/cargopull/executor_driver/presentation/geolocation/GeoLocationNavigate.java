package com.cargopull.executor_driver.presentation.geolocation;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации к решению проблемы с геолокацией.
 */
@StringDef({
    GeoLocationNavigate.RESOLVE_GEO_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface GeoLocationNavigate {

  // Переход к решению проблемы с геолокацией.
  String RESOLVE_GEO_PROBLEM = "GeoLocation.to.ResolveGeoLocationProblem";
}
