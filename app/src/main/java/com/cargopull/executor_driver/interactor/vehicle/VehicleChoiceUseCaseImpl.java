package com.cargopull.executor_driver.interactor.vehicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataUpdateUseCase;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class VehicleChoiceUseCaseImpl implements VehicleChoiceUseCase {

  @NonNull
  private final VehiclesAndOptionsGateway vehiclesAndOptionsGateway;
  @NonNull
  private final DataUpdateUseCase<Vehicle> vehicleChoiceObserver;
  @Nullable
  private List<Vehicle> vehicles;

  @Inject
  public VehicleChoiceUseCaseImpl(
      @NonNull VehiclesAndOptionsGateway vehiclesAndOptionsGateway,
      @NonNull DataUpdateUseCase<Vehicle> vehicleChoiceObserver) {
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
        });
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
      vehicleChoiceObserver.updateWith(vehicle);
      return null;
    });
  }
}
