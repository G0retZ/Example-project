package com.fasten.executor_driver.presentation.choosevehicle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ChooseVehicleViewModelImpl extends ViewModel implements ChooseVehicleViewModel {

  @NonNull
  private final VehicleChoiceUseCase vehicleChoiceUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ChooseVehicleViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  ChooseVehicleViewModelImpl(@NonNull VehicleChoiceUseCase vehicleChoiceUseCase) {
    this.vehicleChoiceUseCase = vehicleChoiceUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new ChooseVehicleViewStatePending());
    navigateLiveData = new SingleLiveEvent<>();
  }


  @NonNull
  @Override
  public LiveData<ViewState<ChooseVehicleViewActions>> getViewStateLiveData() {
    if (disposable == null) {
      loadVehicles();
    }
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setSelection(int index) {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = vehicleChoiceUseCase.setSelectedVehicle(index)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(ChooseVehicleNavigate.VEHICLE_OPTIONS),
            throwable -> {
            }
        );
  }

  private void loadVehicles() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = vehicleChoiceUseCase.getVehicles()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::consumeVehicles, this::consumeError);
  }

  private void consumeVehicles(List<Vehicle> vehicles) {
    ArrayList<ChooseVehicleListItem> chooseVehicleListItems = new ArrayList<>();
    for (Vehicle vehicle : vehicles) {
      chooseVehicleListItems.add(new ChooseVehicleListItem(vehicle));
    }
    viewStateLiveData.postValue(new ChooseVehicleViewStateReady(chooseVehicleListItems));
  }

  private void consumeError(Throwable error) {
    if (error.getClass() == NoVehiclesAvailableException.class) {
      viewStateLiveData.postValue(new ChooseVehicleViewStateError(R.string.no_vehicles_available));
    } else {
      viewStateLiveData.postValue(new ChooseVehicleViewStateError(R.string.error));
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
