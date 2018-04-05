package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final VehicleOptionsGateway gateway;
  @NonNull
  private final DataReceiver<Vehicle> vehicleChoiceReceiver;
  @NonNull
  private final Observer<Vehicle> lastUsedVehicleObserver;
  @NonNull
  private final DataReceiver<List<Option>> driverOptionsReceiver;
  @Nullable
  private Vehicle vehicle;

  @Inject
  public VehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @NonNull DataReceiver<Vehicle> vehicleChoiceReceiver,
      @NonNull Observer<Vehicle> lastUsedVehicleObserver,
      @NonNull DataReceiver<List<Option>> driverOptionsReceiver) {
    this.gateway = gateway;
    this.vehicleChoiceReceiver = vehicleChoiceReceiver;
    this.lastUsedVehicleObserver = lastUsedVehicleObserver;
    this.driverOptionsReceiver = driverOptionsReceiver;
  }

  @NonNull
  @Override
  public Single<List<Option>> getVehicleOptions() {
    return vehicleChoiceReceiver.get()
        .firstOrError()
        .flattenAsObservable(vehicle -> {
          this.vehicle = vehicle;
          return vehicle.getOptions();
        })
        .filter(Option::isVariable)
        .toList();
  }

  @NonNull
  @Override
  public Single<List<Option>> getDriverOptions() {
    return driverOptionsReceiver.get()
        .firstOrError()
        .flattenAsObservable(options -> options)
        .filter(Option::isVariable)
        .toList();
  }

  @Override
  public Completable setSelectedVehicleAndOptions(List<Option> options,
      List<Option> driverOptions) {
    if (vehicle == null) {
      return Completable.error(new DataMappingException());
    }
    vehicle.setOptions(options.toArray(new Option[options.size()]));
    return gateway.sendVehicleOptions(vehicle, driverOptions)
        .doOnComplete(() -> lastUsedVehicleObserver.onNext(vehicle));
  }
}
