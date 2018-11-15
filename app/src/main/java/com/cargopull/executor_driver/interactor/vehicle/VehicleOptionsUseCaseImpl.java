package com.cargopull.executor_driver.interactor.vehicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final VehicleOptionsGateway gateway;
  @NonNull
  private final DataReceiver<Vehicle> vehicleChoiceReceiver;
  @NonNull
  private final LastUsedVehicleGateway lastUsedVehicleGateway;
  @NonNull
  private final VehiclesAndOptionsGateway vehiclesAndOptionsGateway;
  @Nullable
  private Vehicle vehicle;

  @Inject
  public VehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @NonNull DataReceiver<Vehicle> vehicleChoiceReceiver,
      @NonNull LastUsedVehicleGateway lastUsedVehicleGateway,
      @NonNull VehiclesAndOptionsGateway vehiclesAndOptionsGateway) {
    this.gateway = gateway;
    this.vehicleChoiceReceiver = vehicleChoiceReceiver;
    this.lastUsedVehicleGateway = lastUsedVehicleGateway;
    this.vehiclesAndOptionsGateway = vehiclesAndOptionsGateway;
  }

  @NonNull
  @Override
  public Observable<List<Option>> getVehicleOptions() {
    return vehicleChoiceReceiver.get()
        .map(vehicle -> {
          this.vehicle = vehicle;
          return vehicle.getOptions();
        });
  }

  @NonNull
  @Override
  public Single<List<Option>> getDriverOptions() {
    return vehiclesAndOptionsGateway.getExecutorOptions()
        .observeOn(Schedulers.single());
  }

  @Override
  public Completable setSelectedVehicleAndOptions(@NonNull List<Option> options,
      @NonNull List<Option> driverOptions) {
    return Single.fromCallable(() -> {
      if (vehicle == null) {
        throw new IllegalStateException("Не было выбрано ни одного ТС.");
      }
      vehicle.setOptions(options);
      return vehicle;
    }).flatMapCompletable(
        vehicle -> gateway.sendVehicleOptions(vehicle, driverOptions)
            .observeOn(Schedulers.single())
            .concatWith(lastUsedVehicleGateway.saveLastUsedVehicleId(vehicle))
            .observeOn(Schedulers.single())
    );
  }
}
