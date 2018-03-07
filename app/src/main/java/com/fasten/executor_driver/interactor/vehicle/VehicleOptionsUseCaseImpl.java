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
  public VehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @NonNull DataSharer<Vehicle> vehicleChoiceSharer,
      @NonNull DataSharer<Vehicle> lastUsedVehicleSharer,
      @NonNull DataSharer<List<Option>> driverOptionsSharer) {
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
  public Completable setSelectedVehicleAndOptions(List<Option> options,
      List<Option> driverOptions) {
    if (vehicle == null) {
      return Completable.error(new DataMappingException());
    }
    vehicle.setOptions(options.toArray(new Option[options.size()]));
    return gateway.sendVehicleOptions(vehicle, driverOptions)
        .doOnComplete(() -> lastUsedVehicleSharer.share(vehicle));
  }
}
