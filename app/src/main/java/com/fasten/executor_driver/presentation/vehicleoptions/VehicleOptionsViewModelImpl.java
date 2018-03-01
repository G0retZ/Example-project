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
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsViewModelImpl extends ViewModel implements VehicleOptionsViewModel {

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
  VehicleOptionsViewModelImpl(VehicleOptionsUseCase vehicleOptionsUseCase) {
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
  public void setVehicleAndDriverOptions(OptionsListItems optionsListItems) {
    ArrayList<Option> vehicleOptions = new ArrayList<>();
    for (OptionsListItem optionsListItem : optionsListItems.getVehicleOptions()) {
      vehicleOptions.add(optionsListItem.getOption());
    }
    ArrayList<Option> driverOptions = new ArrayList<>();
    for (OptionsListItem optionsListItem : optionsListItems.getDriverOptions()) {
      driverOptions.add(optionsListItem.getOption());
    }
    occupyVehicle(vehicleOptions, driverOptions);
  }

  private void loadOptions() {
    if (optionsDisposable != null && !optionsDisposable.isDisposed()) {
      return;
    }
    optionsDisposable = Observable.combineLatest(
        vehicleOptionsUseCase.getVehicleOptions()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .map(this::map),
        vehicleOptionsUseCase.getDriverOptions()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .map(this::map),
        OptionsListItems::new
    ).subscribe(
        items -> viewStateLiveData.postValue(new VehicleOptionsViewStateReady(items)),
        Throwable::printStackTrace
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
            () -> navigateLiveData.postValue(VehicleOptionsNavigate.READY_FOR_ORDERS),
            throwable -> viewStateLiveData
                .postValue(new VehicleOptionsViewStateError(R.string.no_network_connection))
        );
  }

  private List<OptionsListItem<?>> map(List<Option> options) {
    ArrayList<OptionsListItem<?>> optionsListItems = new ArrayList<>();
    for (Option option : options) {
      if (option instanceof OptionBoolean) {
        optionsListItems
            .add(new OptionsListItem<>((OptionBoolean) option));
      } else if (option instanceof OptionNumeric) {
        optionsListItems
            .add(new OptionsListItem<>((OptionNumeric) option));
      }
    }
    return optionsListItems;
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
