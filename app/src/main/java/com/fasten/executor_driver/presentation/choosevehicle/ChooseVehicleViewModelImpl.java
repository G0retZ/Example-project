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
  private final MutableLiveData<String> navigateLiveData;
  @Nullable
  private Disposable vehiclesDisposable;
  @Nullable
  private Disposable choiceDisposable;

  @Inject
  public ChooseVehicleViewModelImpl(@NonNull VehicleChoiceUseCase vehicleChoiceUseCase) {
    this.vehicleChoiceUseCase = vehicleChoiceUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new ChooseVehicleViewStatePending());
    navigateLiveData = new MutableLiveData<>();
  }


  @NonNull
  @Override
  public LiveData<ViewState<ChooseVehicleViewActions>> getViewStateLiveData() {
    if (vehiclesDisposable == null || vehiclesDisposable.isDisposed()) {
      vehiclesDisposable = vehicleChoiceUseCase.getVehicles()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeVehicles, this::consumeError);
    }
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void selectItem(ChooseVehicleListItem chooseVehicleListItem) {
    if (choiceDisposable != null && !choiceDisposable.isDisposed()) {
      return;
    }
    choiceDisposable = vehicleChoiceUseCase.selectVehicle(chooseVehicleListItem.getVehicle())
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(ChooseVehicleNavigate.VEHICLE_OPTIONS),
            throwable -> {
            }
        );
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
    if (vehiclesDisposable != null) {
      vehiclesDisposable.dispose();
    }
    if (choiceDisposable != null) {
      choiceDisposable.dispose();
    }
  }
}
