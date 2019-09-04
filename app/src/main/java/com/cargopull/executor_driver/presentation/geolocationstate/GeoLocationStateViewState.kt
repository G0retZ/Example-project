package com.cargopull.executor_driver.presentation.geolocationstate

import com.cargopull.executor_driver.R
import com.cargopull.executor_driver.presentation.ImageTextViewActions
import com.cargopull.executor_driver.presentation.ViewState

/**
 * Состояние недоступности обнаружения по GPS.
 */
internal class GeoLocationStateNoGpsDetectionViewState : ViewState<ImageTextViewActions> {

    override fun apply(stateActions: ImageTextViewActions) {
        stateActions.setVisible(-1, true)
        stateActions.setImage(R.id.geoIcon, R.drawable.ic_gps_detection_icon)
        stateActions.setText(R.id.geoTitleText, R.string.turn_on_geo_detection)
        stateActions.setText(R.id.explainText, R.string.gps_detection_required)
    }
}

/**
 * Состояние бездействия вида заказа.
 */
internal class GeoLocationStateNoLocationViewState : ViewState<ImageTextViewActions> {

    override fun apply(stateActions: ImageTextViewActions) {
        stateActions.setVisible(-1, true)
        stateActions.setImage(R.id.geoIcon, R.drawable.ic_geolocation_icon)
        stateActions.setText(R.id.geoTitleText, R.string.turn_on_geolocation)
        stateActions.setText(R.id.explainText, R.string.geolocation_required)
    }
}

/**
 * Состояние недоступности обнаружения по Wi-Fi и мобильным сетям.
 */
internal class GeoLocationStateNoNetworkDetectionViewState : ViewState<ImageTextViewActions> {

    override fun apply(stateActions: ImageTextViewActions) {
        stateActions.setVisible(-1, true)
        stateActions.setImage(R.id.geoIcon, R.drawable.ic_network_detection_icon)
        stateActions.setText(R.id.geoTitleText, R.string.turn_on_geo_detection)
        stateActions.setText(R.id.explainText, R.string.network_detection_required)
    }
}

/**
 * Состояние готовности настроек геолокации.
 */
internal class GeoLocationStateReadyViewState : ViewState<ImageTextViewActions> {

    override fun apply(stateActions: ImageTextViewActions) {
        stateActions.setVisible(-1, false)
    }
}
