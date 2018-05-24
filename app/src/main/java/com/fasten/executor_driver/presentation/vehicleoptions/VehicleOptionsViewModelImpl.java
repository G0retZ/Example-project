package com.fasten.executor_driver.presentation.vehicleoptions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
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
import io.reactivex.internal.disposables.EmptyDisposable;
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
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable optionsDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable occupyDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public VehicleOptionsViewModelImpl(VehicleOptionsUseCase vehicleOptionsUseCase) {
    this.vehicleOptionsUseCase = vehicleOptionsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new VehicleOptionsViewStateInitial());
    navigateLiveData = new SingleLiveEvent<>();
    loadOptions();
  }

  @NonNull
  @Override
  public LiveData<ViewState<VehicleOptionsViewActions>> getViewStateLiveData() {
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
    if (!optionsDisposable.isDisposed()) {
      return;
    }
    optionsDisposable = Observable.combineLatest(
        vehicleOptionsUseCase.getVehicleOptions()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .switchMap(options -> Observable
                .fromIterable(options)
                .<VehicleOptionsListItem<?>>map(this::map)
                .toList()
                .toObservable()
            ),
        vehicleOptionsUseCase.getDriverOptions()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .flattenAsObservable(options -> options)
            .<VehicleOptionsListItem<?>>map(this::map)
            .toList()
            .toObservable(),
        VehicleOptionsListItems::new
    ).subscribe(
        items -> viewStateLiveData.postValue(new VehicleOptionsViewStateReady(items)),
        Throwable::printStackTrace
    );

  }

  private void occupyVehicle(List<Option> vehicleOptions, List<Option> driverOptions) {
    if (!occupyDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new VehicleOptionsViewStatePending());
    occupyDisposable = vehicleOptionsUseCase
        .setSelectedVehicleAndOptions(vehicleOptions, driverOptions)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(VehicleOptionsNavigate.SERVICES),
            throwable -> {
              throwable.printStackTrace();
              viewStateLiveData
                  .postValue(new VehicleOptionsViewStateError(R.string.no_network_connection));
            }
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
    occupyDisposable.dispose();
    optionsDisposable.dispose();
  }
}
