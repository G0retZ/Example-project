package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;
import javax.inject.Inject;

public class SelectedVehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final VehicleOptionsGateway gateway;
  @NonNull
  private final SelectedVehicleOptionsGateway vehiclesAndOptionsGateway;

  @Inject
  public SelectedVehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @NonNull SelectedVehicleOptionsGateway vehiclesAndOptionsGateway) {
    this.gateway = gateway;
    this.vehiclesAndOptionsGateway = vehiclesAndOptionsGateway;
  }

  @NonNull
  @Override
  public Observable<List<Option>> getVehicleOptions() {
    return vehiclesAndOptionsGateway.getVehicleOptions()
        .flattenAsObservable(options -> options)
        .filter(Option::isVariable)
        .toList()
        .toObservable();
  }

  @NonNull
  @Override
  public Single<List<Option>> getDriverOptions() {
    return vehiclesAndOptionsGateway.getExecutorOptions()
        .flattenAsObservable(options -> options)
        .filter(Option::isVariable)
        .toList();
  }

  @Override
  public Completable setSelectedVehicleAndOptions(@NonNull List<Option> options,
      @NonNull List<Option> driverOptions) {
    Vehicle vehicle = new Vehicle(0, "", "", "", "", false);
    vehicle.setOptions(options.toArray(new Option[options.size()]));
    return gateway.sendVehicleOptions(vehicle, driverOptions);
  }
}
