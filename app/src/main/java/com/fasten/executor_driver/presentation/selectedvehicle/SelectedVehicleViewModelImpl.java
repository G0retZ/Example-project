package com.fasten.executor_driver.presentation.selectedvehicle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class SelectedVehicleViewModelImpl extends ViewModel implements SelectedVehicleViewModel {

  @NonNull
  private final SelectedVehicleUseCase vehiclesUseCase;
  @NonNull
  private final MutableLiveData<ViewState<SelectedVehicleViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  public SelectedVehicleViewModelImpl(@NonNull SelectedVehicleUseCase vehiclesUseCase) {
    this.vehiclesUseCase = vehiclesUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new SelectedVehicleViewState(""));
    navigateLiveData = new SingleLiveEvent<>();
  }

  @Override
  public void changeVehicle() {
    navigateLiveData.setValue(SelectedVehicleNavigate.VEHICLES);
  }

  @NonNull
  @Override
  public LiveData<ViewState<SelectedVehicleViewActions>> getViewStateLiveData() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = vehiclesUseCase.getSelectedVehicle()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeVehicle, throwable -> consumeError());
    }
    return viewStateLiveData;
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

  private void consumeError() {
    viewStateLiveData.postValue(new SelectedVehicleViewState(""));
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
