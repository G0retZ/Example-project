package com.cargopull.executor_driver.presentation.geolocation;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации к решению проблем с геолокацией.
 */
@StringDef({
    GeoLocationNavigate.RESOLVE_GEO_PERMISSIONS
})
@Retention(RetentionPolicy.SOURCE)
public @interface GeoLocationNavigate {

  // Переход к решению проблемы с доступом к геолокации.
  String RESOLVE_GEO_PERMISSIONS = "GeoLocation.to.ResolveGeoLocationPermissions";
}
