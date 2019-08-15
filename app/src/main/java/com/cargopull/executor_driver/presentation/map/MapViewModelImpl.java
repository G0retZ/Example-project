package com.cargopull.executor_driver.presentation.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class MapViewModelImpl extends ViewModel implements MapViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final HeatMapUseCase heatMapUseCase;

  @NonNull
  private final MutableLiveData<ViewState<MapViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public MapViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull HeatMapUseCase heatMapUseCase) {
    this.heatMapUseCase = heatMapUseCase;
    this.errorReporter = errorReporter;
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
            errorReporter::reportError
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
