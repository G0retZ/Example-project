package com.fasten.executor_driver.presentation.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.interactor.map.HeatMapUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class MapViewModelImpl extends ViewModel implements MapViewModel {

  private final HeatMapUseCase heatMapUseCase;

  @NonNull
  private final MutableLiveData<ViewState<MapViewActions>> viewStateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  public MapViewModelImpl(HeatMapUseCase heatMapUseCase) {
    this.heatMapUseCase = heatMapUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new MapViewState(null));
  }

  @NonNull
  @Override
  public LiveData<ViewState<MapViewActions>> getViewStateLiveData() {
    subscribeToHeatMapUpdates();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new SingleLiveEvent<>();
  }

  private void subscribeToHeatMapUpdates() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = heatMapUseCase.loadHeatMap()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(geoJson -> viewStateLiveData.postValue(new MapViewState(geoJson)));
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
