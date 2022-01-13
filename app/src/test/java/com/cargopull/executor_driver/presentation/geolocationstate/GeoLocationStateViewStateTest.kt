package com.cargopull.executor_driver.presentation.geolocationstate

import com.cargopull.executor_driver.R
import com.cargopull.executor_driver.presentation.ImageTextViewActions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GeoLocationStateViewStateTest {

    @Mock
    private lateinit var viewActions: ImageTextViewActions

    @Test
    fun testNoGpsDetectionActions() {
        // Given:
        val viewState = GeoLocationStateNoGpsDetectionViewState()

        // Action:
        viewState.apply(viewActions)

        // Effect:
        verify(viewActions).setVisible(-1, true)
        verify(viewActions).setImage(R.id.geoIcon, R.drawable.ic_gps_detection_icon)
        verify(viewActions).setText(R.id.geoTitleText, R.string.turn_on_geo_detection)
        verify(viewActions).setText(R.id.explainText, R.string.gps_detection_required)
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testNoLocationActions() {
        // Given:
        val viewState = GeoLocationStateNoLocationViewState()

        // Action:
        viewState.apply(viewActions)

        // Effect:
        verify(viewActions).setVisible(-1, true)
        verify(viewActions).setImage(R.id.geoIcon, R.drawable.ic_geolocation_icon)
        verify(viewActions).setText(R.id.geoTitleText, R.string.turn_on_geolocation)
        verify(viewActions).setText(R.id.explainText, R.string.geolocation_required)
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testNoNetworkDetectionActions() {
        // Given:
        val viewState = GeoLocationStateNoNetworkDetectionViewState()

        // Action:
        viewState.apply(viewActions)

        // Effect:
        verify(viewActions).setVisible(-1, true)
        verify(viewActions).setImage(R.id.geoIcon, R.drawable.ic_network_detection_icon)
        verify(viewActions).setText(R.id.geoTitleText, R.string.turn_on_geo_detection)
        verify(viewActions).setText(R.id.explainText, R.string.network_detection_required)
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testReadyActions() {
        // Given:
        val viewState = GeoLocationStateReadyViewState()

        // Action:
        viewState.apply(viewActions)

        // Effect:
        verify(viewActions).setVisible(-1, false)
        verifyNoMoreInteractions(viewActions)
    }
}