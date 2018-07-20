package com.cargopull.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
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
      @NonNull ErrorReporter errorReporter,
      @NonNull VehicleOptionsGateway gateway,
      @NonNull DataReceiver<Vehicle> vehicleChoiceReceiver,
      @NonNull LastUsedVehicleGateway lastUsedVehicleGateway,
      @NonNull VehiclesAndOptionsGateway vehiclesAndOptionsGateway) {
    this.errorReporter = errorReporter;
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
          List<Option> result = vehicle.getOptions();
          Iterator<Option> iterator = result.iterator();
          while (iterator.hasNext()) {
            if (!iterator.next().isVariable()) {
              iterator.remove();
            }
          }
          return result;
        });
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
    return Single.fromCallable(() -> {
      if (vehicle == null) {
        throw new IllegalStateException("Не было выбрано ни одного ТС.");
      }
      vehicle.setOptions(options.toArray(new Option[options.size()]));
      return vehicle;
    }).flatMapCompletable(
        vehicle -> gateway.sendVehicleOptions(vehicle, driverOptions)
            .concatWith(lastUsedVehicleGateway.saveLastUsedVehicleId(vehicle))
    ).doOnError(throwable -> {
      if (throwable instanceof IllegalStateException
          || throwable instanceof IllegalArgumentException) {
        errorReporter.reportError(throwable);
      }
    });
  }
}
