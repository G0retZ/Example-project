package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.services.ServicesUseCase;
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
import retrofit2.HttpException;

public class VehicleOptionsViewModelImpl extends ViewModel implements
    VehicleOptionsViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final VehicleOptionsUseCase vehicleOptionsUseCase;
  @NonNull
  private final ServicesUseCase servicesUseCase;
  @NonNull
  private final MutableLiveData<ViewState<VehicleOptionsViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable optionsDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable occupyDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private ViewState<VehicleOptionsViewActions> lastViewState = new VehicleOptionsViewStateInitial();

  @Inject
  public VehicleOptionsViewModelImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull VehicleOptionsUseCase vehicleOptionsUseCase,
      @NonNull ServicesUseCase servicesUseCase) {
    this.errorReporter = errorReporter;
    this.vehicleOptionsUseCase = vehicleOptionsUseCase;
    this.servicesUseCase = servicesUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(lastViewState);
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
        items -> viewStateLiveData.postValue(
            lastViewState = new VehicleOptionsViewStateReady(items)
        ),
        throwable -> {
          if (throwable instanceof DataMappingException) {
            errorReporter.reportError(throwable);
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
        .concatWith(servicesUseCase.autoAssignServices())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
            throwable -> {
              if (throwable instanceof HttpException) {
                int code = ((HttpException) throwable).code();
                if (code >= 500 && code < 600) {
                  errorReporter.reportError(throwable);
                }
              } else {
                errorReporter.reportError(throwable);
              }
              viewStateLiveData.postValue(
                  new VehicleOptionsViewStateResolvableError(
                      R.string.sms_network_error,
                      lastViewState,
                      () -> viewStateLiveData.postValue(lastViewState)
                  )
              );
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
