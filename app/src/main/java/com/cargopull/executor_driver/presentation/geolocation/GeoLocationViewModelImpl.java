package com.cargopull.executor_driver.presentation.geolocation;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.interactor.GeoLocationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class GeoLocationViewModelImpl extends ViewModel implements GeoLocationViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final GeoLocationUseCase geoLocationUseCase;

  @NonNull
  private final MutableLiveData<ViewState<GeoLocationViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public GeoLocationViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull GeoLocationUseCase geoLocationUseCase) {
    this.errorReporter = errorReporter;
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
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            location -> viewStateLiveData
                .postValue(new GeoLocationViewState(location)),
            throwable -> {
              errorReporter.reportError(throwable);
              if (throwable instanceof SecurityException) {
                navigateLiveData.postValue(GeoLocationNavigate.RESOLVE_GEO_PERMISSIONS);
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
