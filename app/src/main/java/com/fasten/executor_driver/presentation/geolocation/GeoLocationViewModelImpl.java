package com.fasten.executor_driver.presentation.geolocation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.interactor.DataReceiver;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class GeoLocationViewModelImpl extends ViewModel implements GeoLocationViewModel {

  @NonNull
  private final DataReceiver<GeoLocation> geoLocationReceiver;

  @NonNull
  private final MutableLiveData<ViewState<GeoLocationViewActions>> viewStateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  GeoLocationViewModelImpl(@NonNull DataReceiver<GeoLocation> geoLocationReceiver) {
    this.geoLocationReceiver = geoLocationReceiver;
    viewStateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<GeoLocationViewActions>> getViewStateLiveData() {
    subscribeToLocationUpdates();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new SingleLiveEvent<>();
  }

  private void subscribeToLocationUpdates() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = geoLocationReceiver.get()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(location -> viewStateLiveData.postValue(new GeoLocationViewState(location)),
            throwable -> subscribeToLocationUpdates(), this::subscribeToLocationUpdates);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
