package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsViewModelImpl extends ViewModel implements
    VehicleOptionsViewModel {

  @NonNull
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
  public VehicleOptionsViewModelImpl(@NonNull VehicleOptionsUseCase vehicleOptionsUseCase) {
    this.vehicleOptionsUseCase = vehicleOptionsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new VehicleOptionsViewStateInitial());
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
            .observeOn(AndroidSchedulers.mainThread())
            .switchMap(options -> Observable
                .fromIterable(options)
                .<VehicleOptionsListItem<?>>map(this::map)
                .toList()
                .toObservable()
            ),
        vehicleOptionsUseCase.getDriverOptions()
            .observeOn(AndroidSchedulers.mainThread())
            .flattenAsObservable(options -> options)
            .<VehicleOptionsListItem<?>>map(this::map)
            .toList()
            .toObservable(),
        VehicleOptionsListItems::new
    ).subscribe(
        items -> viewStateLiveData.postValue(new VehicleOptionsViewStateReady(items)),
        throwable -> {
          if (throwable instanceof DataMappingException) {
            navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
          } else {
            navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
          }
        }
    );

  }

  private void occupyVehicle(List<Option> vehicleOptions, List<Option> driverOptions) {
    if (!occupyDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new VehicleOptionsViewStatePending());
    occupyDisposable = vehicleOptionsUseCase
        .setSelectedVehicleAndOptions(vehicleOptions, driverOptions)
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
    occupyDisposable.dispose();
    optionsDisposable.dispose();
  }
}
