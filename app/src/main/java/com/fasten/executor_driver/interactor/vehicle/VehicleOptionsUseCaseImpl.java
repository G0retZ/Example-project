package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final VehicleOptionsGateway gateway;
  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;
  @NonNull
  private final DataSharer<Vehicle> lastUsedVehicleSharer;
  @NonNull
  private final DataSharer<List<Option>> driverOptionsSharer;
  @Nullable
  private Vehicle vehicle;

  @Inject
  VehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer,
      @Named("lastUsedVehicleSharer") @NonNull DataSharer<Vehicle> lastUsedVehicleSharer,
      @Named("driverOptionsSharer") @NonNull DataSharer<List<Option>> driverOptionsSharer) {
    this.gateway = gateway;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
    this.lastUsedVehicleSharer = lastUsedVehicleSharer;
    this.driverOptionsSharer = driverOptionsSharer;
  }

  @NonNull
  @Override
  public Observable<List<Option>> getVehicleOptions() {
    return vehicleChoiceSharer.get()
        .concatMap(vehicle -> {
          this.vehicle = vehicle;
          return Observable.fromIterable(vehicle.getOptions())
              .filter(Option::isVariable)
              .toList()
              .toObservable();
        });
  }

  @NonNull
  @Override
  public Observable<List<Option>> getDriverOptions() {
    return driverOptionsSharer.get()
        .concatMap(options -> Observable.fromIterable(options)
            .filter(Option::isVariable)
            .toList()
            .toObservable());
  }

  @Override
  public Completable setSelectedVehicleOptions(List<Option> options, List<Option> driverOptions) {
    if (vehicle == null) {
      return Completable.error(new DataMappingException());
    }
    vehicle.setOptions(options.toArray(new Option[options.size()]));
    return gateway.sendVehicleOptions(vehicle, driverOptions)
        .doOnComplete(() -> lastUsedVehicleSharer.share(vehicle));
  }
}
