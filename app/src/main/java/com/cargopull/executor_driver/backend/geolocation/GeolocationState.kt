package com.cargopull.executor_driver.backend.geolocation

import android.location.LocationManager

/**
 * Проверка состояния геолокации.
 */
interface GeolocationState {

    /**
     * Проверить, работает ли определение местоположения по GPS.
     * @return [Boolean] состояние.
     */
    val isGpsEnabled: Boolean

    /**
     * Проверить, работает ли определение местоположения по сотовым сетям и WiFi.
     * @return [Boolean] состояние.
     */
    val isNetworkEnabled: Boolean
}

class GeolocationStateImpl(private val locationManager: LocationManager): GeolocationState {
    override val isGpsEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    override val isNetworkEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}