package com.cargopull.executor_driver.presentation.geolocationstate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cargopull.executor_driver.backend.analytics.EventLogger
import com.cargopull.executor_driver.backend.geolocation.GeolocationState
import com.cargopull.executor_driver.interactor.CommonGateway
import com.cargopull.executor_driver.presentation.ImageTextViewActions
import com.cargopull.executor_driver.presentation.ViewModel
import com.cargopull.executor_driver.presentation.ViewState
import com.cargopull.executor_driver.utils.TimeUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * ViewModel окна состояния сервисов местоположения.
 */
interface GeoLocationStateViewModel : ViewModel<ImageTextViewActions> {

    /**
     * Запрашивает повторную проверку настроек.
     */
    fun checkSettings()
}

class GeoLocationStateViewModelImpl(
        private val eventLogger: EventLogger,
        private val geolocationState: GeolocationState,
        private val timeUtils: TimeUtils,
        geoLocationGateway: CommonGateway<Boolean>) : androidx.lifecycle.ViewModel(), GeoLocationStateViewModel {
    private val viewStateLiveData: MutableLiveData<ViewState<ImageTextViewActions>>
    private val disposable: Disposable
    private var wasAvailable = false
    private var wasGpsOn = false
    private var wasNetworkOn = false
    private var timeStamp: Long = -1

    init {
        viewStateLiveData = MutableLiveData()
        disposable = geoLocationGateway.data
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::consumeState, Throwable::printStackTrace)
    }

    override fun getViewStateLiveData(): LiveData<ViewState<ImageTextViewActions>> {
        return viewStateLiveData
    }

    override fun getNavigationLiveData(): LiveData<String> {
        return MutableLiveData()
    }

    override fun checkSettings() {
        consumeState(wasAvailable)
    }

    private fun consumeState(available: Boolean) {
        val gpsOn = geolocationState.isGpsEnabled
        val networkOn = geolocationState.isNetworkEnabled
        if (wasAvailable && (wasGpsOn || wasNetworkOn) && !available && (gpsOn || networkOn)) {
            timeStamp = timeUtils.currentTimeMillis()
        } else if (timeStamp > 0) {
            timeStamp = when {
                !gpsOn && !networkOn -> reportGeolocationState("geolocation_lost")
                available -> reportGeolocationState("geolocation_restored")
                else -> timeStamp
            }
        }
        wasAvailable = available
        wasGpsOn = gpsOn
        wasNetworkOn = networkOn
        viewStateLiveData.postValue(
                when {
                    gpsOn && networkOn -> GeoLocationStateReadyViewState()
                    networkOn -> GeoLocationStateNoGpsDetectionViewState()
                    gpsOn -> GeoLocationStateNoNetworkDetectionViewState()
                    else -> GeoLocationStateNoLocationViewState()
                }
        )
    }

    private fun reportGeolocationState(event: String): Long {
        eventLogger.reportEvent(event,
                mutableMapOf("loss_duration" to "${timeUtils.currentTimeMillis() - timeStamp}"))
        return -1
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
