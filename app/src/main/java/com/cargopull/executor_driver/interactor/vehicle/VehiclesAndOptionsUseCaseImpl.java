package com.cargopull.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.NoFreeVehiclesException;
import com.cargopull.executor_driver.entity.NoVehiclesAvailableException;
import com.cargopull.executor_driver.entity.Vehicle;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import javax.inject.Inject;

public class VehiclesAndOptionsUseCaseImpl implements VehiclesAndOptionsUseCase {

  @NonNull
  private final VehiclesAndOptionsGateway gateway;
  @NonNull
  private final Observer<Vehicle> vehicleChoiceObserver;
  @NonNull
  private final LastUsedVehicleGateway lastUsedVehicleGateway;

  @Inject
  public VehiclesAndOptionsUseCaseImpl(@NonNull VehiclesAndOptionsGateway gateway,
      @NonNull Observer<Vehicle> vehicleChoiceObserver,
      @NonNull LastUsedVehicleGateway lastUsedVehicleGateway) {
    this.gateway = gateway;
    this.vehicleChoiceObserver = vehicleChoiceObserver;
    this.lastUsedVehicleGateway = lastUsedVehicleGateway;
  }

  @NonNull
  @Override
  public Completable loadVehiclesAndOptions() {
    return lastUsedVehicleGateway.getLastUsedVehicleId()
        .onErrorResumeNext(Single.just(-1L))
        .flatMapCompletable(
            vehicleId -> gateway
                .getExecutorVehicles()
                .doOnSuccess(list -> {
                  if (list.isEmpty()) {
                    throw new NoVehiclesAvailableException();
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
                    throw new NoFreeVehiclesException();
                  } else {
                    vehicleChoiceObserver.onNext(freeVehicle);
                  }
                }).toCompletable()
        );
  }
}
