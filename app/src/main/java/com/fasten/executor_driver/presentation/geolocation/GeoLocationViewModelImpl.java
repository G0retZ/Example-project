package com.fasten.executor_driver.presentation.geolocation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.interactor.GeoLocationUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class GeoLocationViewModelImpl extends ViewModel implements GeoLocationViewModel {

  @NonNull
  private final GeoLocationUseCase geoLocationUseCase;

  @NonNull
  private final MutableLiveData<ViewState<GeoLocationViewActions>> viewStateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  public GeoLocationViewModelImpl(@NonNull GeoLocationUseCase geoLocationUseCase) {
    this.geoLocationUseCase = geoLocationUseCase;
    viewStateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<GeoLocationViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new SingleLiveEvent<>();
  }

  @Override
  public void updateGeoLocations() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = geoLocationUseCase.getGeoLocations()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            location -> viewStateLiveData.postValue(new GeoLocationViewState(location)),
            throwable -> viewStateLiveData.postValue(new GeoLocationViewStateError())
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
