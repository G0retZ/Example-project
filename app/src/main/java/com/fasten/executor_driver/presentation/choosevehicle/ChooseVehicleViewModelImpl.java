package com.fasten.executor_driver.presentation.choosevehicle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
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
  @NonNull
  private Disposable vehiclesDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable choiceDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public ChooseVehicleViewModelImpl(@NonNull VehicleChoiceUseCase vehicleChoiceUseCase) {
    this.vehicleChoiceUseCase = vehicleChoiceUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new MutableLiveData<>();
    loadVehicles();
  }


  @NonNull
  @Override
  public LiveData<ViewState<ChooseVehicleViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void selectItem(ChooseVehicleListItem chooseVehicleListItem) {
    if (!choiceDisposable.isDisposed()) {
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

  private void loadVehicles() {
    if (!vehiclesDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new ChooseVehicleViewStatePending());
    vehiclesDisposable = vehicleChoiceUseCase.getVehicles()
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
    vehiclesDisposable.dispose();
    choiceDisposable.dispose();
  }
}
