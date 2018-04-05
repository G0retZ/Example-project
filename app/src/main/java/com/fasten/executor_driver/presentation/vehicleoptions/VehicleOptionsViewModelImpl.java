package com.fasten.executor_driver.presentation.vehicleoptions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsViewModelImpl extends ViewModel implements
    VehicleOptionsViewModel {

  private final VehicleOptionsUseCase vehicleOptionsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<VehicleOptionsViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @Nullable
  private Disposable optionsDisposable;
  @Nullable
  private Disposable occupyDisposable;

  @Inject
  public VehicleOptionsViewModelImpl(VehicleOptionsUseCase vehicleOptionsUseCase) {
    this.vehicleOptionsUseCase = vehicleOptionsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new VehicleOptionsViewStateInitial());
    navigateLiveData = new MutableLiveData<>();
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
  public void setOptions(VehicleOptionsListItems vehicleOptionsListItems) {
    ArrayList<Option> vehicleOptions = new ArrayList<>();
    for (VehicleOptionsListItem vehicleOptionsListItem : vehicleOptionsListItems
        .getVehicleOptions()) {
      vehicleOptions.add(vehicleOptionsListItem.getOption());
    }
    ArrayList<Option> driverOptions = new ArrayList<>();
    for (VehicleOptionsListItem vehicleOptionsListItem : vehicleOptionsListItems
        .getDriverOptions()) {
      driverOptions.add(vehicleOptionsListItem.getOption());
    }
    occupyVehicle(vehicleOptions, driverOptions);
  }

  private void loadOptions() {
    if (optionsDisposable != null && !optionsDisposable.isDisposed()) {
      return;
    }
    optionsDisposable = Single.zip(
        vehicleOptionsUseCase.getVehicleOptions()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .flattenAsObservable(options -> options)
            .<VehicleOptionsListItem<?>>map(this::map)
            .toList(),
        vehicleOptionsUseCase.getDriverOptions()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .flattenAsObservable(options -> options)
            .<VehicleOptionsListItem<?>>map(this::map)
            .toList(),
        VehicleOptionsListItems::new
    ).subscribe(
        items -> viewStateLiveData.postValue(new VehicleOptionsViewStateReady(items)),
        throwable -> {
        }
    );

  }

  private void occupyVehicle(List<Option> vehicleOptions, List<Option> driverOptions) {
    if (occupyDisposable != null && !occupyDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new VehicleOptionsViewStatePending());
    occupyDisposable = vehicleOptionsUseCase
        .setSelectedVehicleAndOptions(vehicleOptions, driverOptions)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(VehicleOptionsNavigate.SERVICES),
            throwable -> viewStateLiveData
                .postValue(new VehicleOptionsViewStateError(R.string.no_network_connection))
        );
  }

  private VehicleOptionsListItem<?> map(Option option) {
    if (option instanceof OptionBoolean) {
      return new VehicleOptionsListItem<>((OptionBoolean) option);
    } else if (option instanceof OptionNumeric) {
      return new VehicleOptionsListItem<>((OptionNumeric) option);
    }
    return null;
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
