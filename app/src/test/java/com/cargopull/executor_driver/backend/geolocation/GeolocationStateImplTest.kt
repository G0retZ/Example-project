package com.cargopull.executor_driver.backend.geolocation

import android.location.LocationManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GeolocationStateImplTest {

    @Mock
    private lateinit var locationManager: LocationManager

    @Test
    fun gpsStateInteraction() {
        // Дано:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Действие:
        geolocationState.isGpsEnabled

        // Результат:
        verify(locationManager, only()).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @Test
    fun networkStateInteraction() {
        // Дано:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Действие:
        geolocationState.isNetworkEnabled

        // Результат:
        verify(locationManager, only()).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @Test
    fun gpsStateAnswer() {
        // Дано:
        `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true, false)

        // Действие:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Результат:
        assertTrue(geolocationState.isGpsEnabled)
        assertFalse(geolocationState.isGpsEnabled)
        assertFalse(geolocationState.isGpsEnabled)
    }

    @Test
    fun networkStateAnswer() {
        // Дано:
        `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false, true)

        // Действие:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Результат:
        assertFalse(geolocationState.isNetworkEnabled)
        assertTrue(geolocationState.isNetworkEnabled)
        assertTrue(geolocationState.isNetworkEnabled)
    }
}