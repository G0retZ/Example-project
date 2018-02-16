package com.fasten.executor_driver.presentation.vehicleoptions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.NoVehicleOptionsAvailableException;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

class VehicleOptionsViewModelImpl extends ViewModel implements VehicleOptionsViewModel {

  private final VehicleOptionsUseCase vehicleOptionsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<VehicleOptionsViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @Nullable
  private Disposable optionsDisposable;
  @Nullable
  private Disposable occupyDisposable;

  @Inject
  VehicleOptionsViewModelImpl(
      VehicleOptionsUseCase vehicleOptionsUseCase) {
    this.vehicleOptionsUseCase = vehicleOptionsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new VehicleOptionsViewStateInitial());
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<VehicleOptionsViewActions>> getViewStateLiveData() {
    if (optionsDisposable == null) {
      loadOptions();
    }
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setVehicleOptions(List<VehicleOptionsListItem<?>> index) {
    ArrayList<VehicleOption> vehicleOptions = new ArrayList<>();
    for (VehicleOptionsListItem vehicleOptionsListItem : index) {
      vehicleOptions.add(vehicleOptionsListItem.getVehicleOption());
    }
    occupyVehicle(vehicleOptions);
  }

  private void loadOptions() {
    if (optionsDisposable != null && !optionsDisposable.isDisposed()) {
      return;
    }
    optionsDisposable = vehicleOptionsUseCase.getVehicleOptions()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            list -> viewStateLiveData.postValue(new VehicleOptionsViewStateReady(map(list))),
            throwable -> {
              if (throwable instanceof NoVehicleOptionsAvailableException) {
                occupyVehicle(new ArrayList<>());
              }
            }
        );
  }

  private void occupyVehicle(List<VehicleOption> vehicleOptions) {
    if (occupyDisposable != null && !occupyDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new VehicleOptionsViewStatePending());
    occupyDisposable = vehicleOptionsUseCase.setSelectedVehicleOptions(vehicleOptions)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(VehicleOptionsNavigate.READY_FOR_ORDERS),
            throwable -> viewStateLiveData
                .postValue(new VehicleOptionsViewStateError(R.string.no_network_connection))
        );
  }

  private List<VehicleOptionsListItem<?>> map(List<VehicleOption> vehicleOptions) {
    ArrayList<VehicleOptionsListItem<?>> vehicleOptionsListItems = new ArrayList<>();
    for (VehicleOption vehicleOption : vehicleOptions) {
      if (vehicleOption instanceof VehicleOptionBoolean) {
        vehicleOptionsListItems
            .add(new VehicleOptionsListItem<>((VehicleOptionBoolean) vehicleOption));
      } else if (vehicleOption instanceof VehicleOptionNumeric) {
        vehicleOptionsListItems
            .add(new VehicleOptionsListItem<>((VehicleOptionNumeric) vehicleOption));
      }
    }
    return vehicleOptionsListItems;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (occupyDisposable != null) {
      occupyDisposable.dispose();
    }
    if (optionsDisposable != null) {
      optionsDisposable.dispose();
    }
  }
}
