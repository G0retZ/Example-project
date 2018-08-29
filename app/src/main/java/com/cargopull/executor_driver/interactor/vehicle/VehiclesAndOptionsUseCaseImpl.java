package com.cargopull.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.NoSuchElementException;
import javax.inject.Inject;

public class VehiclesAndOptionsUseCaseImpl implements VehiclesAndOptionsUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final VehiclesAndOptionsGateway gateway;
  @NonNull
  private final Observer<Vehicle> vehicleChoiceObserver;
  @NonNull
  private final LastUsedVehicleGateway lastUsedVehicleGateway;

  @Inject
  public VehiclesAndOptionsUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull VehiclesAndOptionsGateway gateway,
      @NonNull Observer<Vehicle> vehicleChoiceObserver,
      @NonNull LastUsedVehicleGateway lastUsedVehicleGateway) {
    this.errorReporter = errorReporter;
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
                    vehicleChoiceObserver.onNext(freeVehicle);
                  }
                }).toCompletable()
        ).doOnError(throwable -> {
          if (throwable instanceof DriverBlockedException
              || throwable instanceof EmptyListException
              || throwable instanceof NoSuchElementException) {
            errorReporter.reportError(throwable);
          }
        });
  }
}
