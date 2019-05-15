package com.cargopull.executor_driver.interactor.vehicle;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataUpdateUseCase;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.NoSuchElementException;
import javax.inject.Inject;

public class VehiclesAndOptionsUseCaseImpl implements VehiclesAndOptionsUseCase {

  @NonNull
  private final VehiclesAndOptionsGateway gateway;
  @NonNull
  private final DataUpdateUseCase<Vehicle> vehicleChoiceObserver;
  @NonNull
  private final LastUsedVehicleGateway lastUsedVehicleGateway;

  @Inject
  public VehiclesAndOptionsUseCaseImpl(
      @NonNull VehiclesAndOptionsGateway gateway,
      @NonNull DataUpdateUseCase<Vehicle> vehicleChoiceObserver,
      @NonNull LastUsedVehicleGateway lastUsedVehicleGateway) {
    this.gateway = gateway;
    this.vehicleChoiceObserver = vehicleChoiceObserver;
    this.lastUsedVehicleGateway = lastUsedVehicleGateway;
  }

  @NonNull
  @Override
  public Completable loadVehiclesAndOptions() {
    return lastUsedVehicleGateway.getLastUsedVehicleId()
        .observeOn(Schedulers.single())
        .onErrorResumeNext(Single.just(-1L))
        .flatMapCompletable(
            vehicleId -> gateway
                .getExecutorVehicles()
                .observeOn(Schedulers.single())
                .doOnSuccess(list -> {
                  if (list.isEmpty()) {
                    throw new EmptyListException("Нет ТС доступных для исполнителя.");
                  }
                  Vehicle freeVehicle = null;
                  for (Vehicle vehicle : list) {
                    if (vehicle.isBusy()) {
                      continue;
                    }
                    if (freeVehicle == null || vehicle.getId() == vehicleId) {
                      freeVehicle = vehicle;
                    }
                  }
                  if (freeVehicle == null) {
                    throw new NoSuchElementException("Нет свободных ТС. Доступные ТС:" + list);
                  } else {
                    vehicleChoiceObserver.updateWith(freeVehicle);
                  }
                }).ignoreElement()
        );
  }
}
