package com.cargopull.executor_driver.presentation.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class MapViewModelImpl extends ViewModel implements MapViewModel {

  @NonNull
  private final HeatMapUseCase heatMapUseCase;

  @NonNull
  private final MutableLiveData<ViewState<MapViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public MapViewModelImpl(@NonNull HeatMapUseCase heatMapUseCase) {
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
    return new MutableLiveData<>();
  }

  private void subscribeToHeatMapUpdates() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = heatMapUseCase.loadHeatMap()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            geoJson -> viewStateLiveData.postValue(new MapViewState(geoJson)),
            throwable -> {
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
