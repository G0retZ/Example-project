package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;

public class GeoLocationStateViewModelImpl extends ViewModel implements GeoLocationStateViewModel {

  @NonNull
  private final MutableLiveData<ViewState<GeoLocationStateViewActions>> viewStateLiveData;
  @NonNull
  private final Disposable disposable;

  @Inject
  public GeoLocationStateViewModelImpl(@NonNull CommonGateway<Boolean> geoLocationGateway) {
    viewStateLiveData = new MutableLiveData<>();
    disposable = geoLocationGateway.getData()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            state -> viewStateLiveData.postValue(viewState -> viewState.showGeolocationState(state)),
            Throwable::printStackTrace
        );
  }

  @NonNull
  @Override
  public LiveData<ViewState<GeoLocationStateViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
