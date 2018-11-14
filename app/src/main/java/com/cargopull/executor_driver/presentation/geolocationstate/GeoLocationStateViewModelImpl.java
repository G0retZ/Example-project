package com.cargopull.executor_driver.presentation.geolocationstate;

import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.ViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.HashMap;
import javax.inject.Inject;

public class GeoLocationStateViewModelImpl extends ViewModel implements GeoLocationStateViewModel {

  @NonNull
  private final EventLogger eventLogger;
  @NonNull
  private final LocationManager locationManager;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private final MutableLiveData<ViewState<ViewActions>> viewStateLiveData;
  @NonNull
  private final Disposable disposable;
  private boolean wasAvailable = false;
  private boolean wasGpsOn = false;
  private boolean wasNetworkOn = false;
  private long timeStamp = -1;

  @Inject
  public GeoLocationStateViewModelImpl(
      @NonNull EventLogger eventLogger,
      @NonNull LocationManager locationManager,
      @NonNull TimeUtils timeUtils,
      @NonNull CommonGateway<Boolean> geoLocationGateway) {
    this.eventLogger = eventLogger;
    this.locationManager = locationManager;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    disposable = geoLocationGateway.getData()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::consumeState,
            Throwable::printStackTrace
        );
  }

  @NonNull
  @Override
  public LiveData<ViewState<ViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void checkSettings() {
    consumeState(wasAvailable);
  }

  private void consumeState(boolean available) {
    boolean gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    if (wasAvailable && (wasGpsOn || wasNetworkOn) && !available && (gpsOn || networkOn)) {
      timeStamp = timeUtils.currentTimeMillis();
    } else if (timeStamp > 0) {
      if (!gpsOn && !networkOn) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loss_duration", String.valueOf(timeUtils.currentTimeMillis() - timeStamp));
        eventLogger.reportEvent("geolocation_lost", params);
        timeStamp = -1;
      } else if (available) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loss_duration", String.valueOf(timeUtils.currentTimeMillis() - timeStamp));
        eventLogger.reportEvent("geolocation_restored", params);
        timeStamp = -1;
      }
    }
    wasAvailable = available;
    wasGpsOn = gpsOn;
    wasNetworkOn = networkOn;
    if (gpsOn && networkOn) {
      viewStateLiveData.postValue(new GeoLocationStateReadyViewState());
    } else if (networkOn) {
      viewStateLiveData.postValue(new GeoLocationStateNoGpsDetectionViewState());
    } else if (gpsOn) {
      viewStateLiveData.postValue(new GeoLocationStateNoNetworkDetectionViewState());
    } else {
      viewStateLiveData.postValue(new GeoLocationStateNoLocationViewState());
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
