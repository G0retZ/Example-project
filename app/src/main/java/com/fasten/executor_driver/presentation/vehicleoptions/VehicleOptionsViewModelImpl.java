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
import com.fasten.executor_driver.presentation.options.OptionsListItem;
import com.fasten.executor_driver.presentation.options.OptionsListItems;
import com.fasten.executor_driver.presentation.options.OptionsViewActions;
import com.fasten.executor_driver.presentation.options.OptionsViewModel;
import com.fasten.executor_driver.presentation.options.OptionsViewStateError;
import com.fasten.executor_driver.presentation.options.OptionsViewStateInitial;
import com.fasten.executor_driver.presentation.options.OptionsViewStatePending;
import com.fasten.executor_driver.presentation.options.OptionsViewStateReady;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsViewModelImpl extends ViewModel implements OptionsViewModel {

  private final VehicleOptionsUseCase vehicleOptionsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OptionsViewActions>> viewStateLiveData;
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
    viewStateLiveData.postValue(new OptionsViewStateInitial());
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OptionsViewActions>> getViewStateLiveData() {
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
  public void setOptions(OptionsListItems optionsListItems) {
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
        items -> viewStateLiveData.postValue(new OptionsViewStateReady(items)),
        Throwable::printStackTrace
    );

  }

  private void occupyVehicle(List<Option> vehicleOptions, List<Option> driverOptions) {
    if (occupyDisposable != null && !occupyDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OptionsViewStatePending());
    occupyDisposable = vehicleOptionsUseCase
        .setSelectedVehicleAndOptions(vehicleOptions, driverOptions)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(VehicleOptionsNavigate.SERVICES),
            throwable -> viewStateLiveData
                .postValue(new OptionsViewStateError(R.string.no_network_connection))
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
