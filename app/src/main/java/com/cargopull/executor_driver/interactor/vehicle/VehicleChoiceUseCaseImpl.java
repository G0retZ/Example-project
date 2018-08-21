package com.cargopull.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class VehicleChoiceUseCaseImpl implements VehicleChoiceUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final VehiclesAndOptionsGateway vehiclesAndOptionsGateway;
  @NonNull
  private final Observer<Vehicle> vehicleChoiceObserver;
  @Nullable
  private List<Vehicle> vehicles;

  @Inject
  public VehicleChoiceUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull VehiclesAndOptionsGateway vehiclesAndOptionsGateway,
      @NonNull Observer<Vehicle> vehicleChoiceObserver) {
    this.errorReporter = errorReporter;
    this.vehiclesAndOptionsGateway = vehiclesAndOptionsGateway;
    this.vehicleChoiceObserver = vehicleChoiceObserver;
  }

  @NonNull
  @Override
  public Single<List<Vehicle>> getVehicles() {
    return vehiclesAndOptionsGateway.getExecutorVehicles()
        .observeOn(Schedulers.single())
        .map(list -> {
          if (list.isEmpty()) {
            throw new EmptyListException("Нет ТС доступных для исполнителя.");
          }
          vehicles = list;
          return list;
        }).doOnError(errorReporter::reportError);
  }

  @Override
  public Completable selectVehicle(Vehicle vehicle) {
    return Completable.fromCallable(() -> {
      if (vehicles == null || !vehicles.contains(vehicle)) {
        throw new IndexOutOfBoundsException("Нет такого ТС в списке: " + vehicle
            + ". Доступные ТС: " + vehicles);
      }
      if (vehicle.isBusy()) {
        throw new IllegalArgumentException("Это ТС занято: " + vehicle);
      }
      vehicleChoiceObserver.onNext(vehicle);
      return null;
    }).doOnError(throwable -> {
      if (throwable instanceof IllegalArgumentException
          || throwable instanceof EmptyListException
          || throwable instanceof IndexOutOfBoundsException) {
        errorReporter.reportError(throwable);
      }
    });
  }
}
