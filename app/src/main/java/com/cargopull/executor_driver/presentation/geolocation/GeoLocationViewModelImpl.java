package com.cargopull.executor_driver.presentation.geolocation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.GeoLocationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class GeoLocationViewModelImpl extends ViewModel implements GeoLocationViewModel {

  @NonNull
  private final GeoLocationUseCase geoLocationUseCase;

  @NonNull
  private final MutableLiveData<ViewState<GeoLocationViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public GeoLocationViewModelImpl(@NonNull GeoLocationUseCase geoLocationUseCase) {
    this.geoLocationUseCase = geoLocationUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<GeoLocationViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void updateGeoLocations() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = geoLocationUseCase.getGeoLocations()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            location -> viewStateLiveData
                .postValue(new GeoLocationViewState(location)),
            throwable -> {
              if (throwable instanceof SecurityException) {
                navigateLiveData.postValue(GeoLocationNavigate.RESOLVE_GEO_PROBLEM);
              } else if (!(throwable instanceof IllegalStateException)) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
