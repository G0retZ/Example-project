package com.cargopull.executor_driver.presentation.selectedvehicle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class SelectedVehicleViewModelImpl extends ViewModel implements SelectedVehicleViewModel {

  @NonNull
  private final SelectedVehicleUseCase vehiclesUseCase;
  @NonNull
  private final MutableLiveData<ViewState<SelectedVehicleViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public SelectedVehicleViewModelImpl(@NonNull SelectedVehicleUseCase vehiclesUseCase) {
    this.vehiclesUseCase = vehiclesUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new SelectedVehicleViewState(""));
  }

  @Override
  public void changeVehicle() {
    navigateLiveData.setValue(SelectedVehicleNavigate.VEHICLES);
  }

  @NonNull
  @Override
  public LiveData<ViewState<SelectedVehicleViewActions>> getViewStateLiveData() {
    loadSelectedVehicle();
    return viewStateLiveData;
  }

  private void loadSelectedVehicle() {
    if (disposable.isDisposed()) {
      disposable = vehiclesUseCase.getSelectedVehicle()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .doAfterTerminate(this::loadSelectedVehicle)
          .subscribe(this::consumeVehicle, this::consumeError);
    }
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void consumeVehicle(@NonNull Vehicle vehicle) {

    viewStateLiveData.postValue(new SelectedVehicleViewState(
        vehicle.getManufacturer() + " " + vehicle.getModel()
            + " (" + vehicle.getLicensePlate() + ")"
    ));
  }

  private void consumeError(Throwable throwable) {
    viewStateLiveData.postValue(new SelectedVehicleViewState(""));
    if (throwable instanceof DataMappingException) {
      navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
