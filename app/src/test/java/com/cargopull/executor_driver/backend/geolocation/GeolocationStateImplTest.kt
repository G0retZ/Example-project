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
        // Given:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Action:
        geolocationState.isGpsEnabled

        // Effect:
        verify(locationManager, only()).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @Test
    fun networkStateInteraction() {
        // Given:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Action:
        geolocationState.isNetworkEnabled

        // Effect:
        verify(locationManager, only()).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @Test
    fun gpsStateAnswer() {
        // Given:
        `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true, false)

        // Action:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Effect:
        assertTrue(geolocationState.isGpsEnabled)
        assertFalse(geolocationState.isGpsEnabled)
        assertFalse(geolocationState.isGpsEnabled)
    }

    @Test
    fun networkStateAnswer() {
        // Given:
        `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false, true)

        // Action:
        val geolocationState = GeolocationStateImpl(locationManager)

        // Effect:
        assertFalse(geolocationState.isNetworkEnabled)
        assertTrue(geolocationState.isNetworkEnabled)
        assertTrue(geolocationState.isNetworkEnabled)
    }
}