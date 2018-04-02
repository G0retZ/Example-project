package com.fasten.executor_driver.interactor.vehicle;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Observer;
import java.util.List;
import javax.inject.Inject;

public class VehiclesAndOptionsUseCaseImpl implements VehiclesAndOptionsUseCase {

  @NonNull
  private final VehiclesAndOptionsGateway gateway;
  @NonNull
  private final Observer<List<Vehicle>> vehiclesObserver;
  @NonNull
  private final Observer<List<Option>> driverOptionsObserver;
  @NonNull
  private final Observer<Vehicle> vehicleChoiceObserver;
  @Nullable
  private Vehicle lastUsedVehicle;

  @Inject
  public VehiclesAndOptionsUseCaseImpl(@NonNull VehiclesAndOptionsGateway gateway,
      @NonNull Observer<List<Vehicle>> vehiclesObserver,
      @NonNull Observer<List<Option>> driverOptionsObserver,
      @NonNull Observer<Vehicle> vehicleChoiceObserver,
      @NonNull DataReceiver<Vehicle> lastUsedVehicleReceiver) {
    this.gateway = gateway;
    this.vehiclesObserver = vehiclesObserver;
    this.driverOptionsObserver = driverOptionsObserver;
    this.vehicleChoiceObserver = vehicleChoiceObserver;
    loadLastUsedVehicle(lastUsedVehicleReceiver);
  }

  @SuppressLint("CheckResult")
  private void loadLastUsedVehicle(@NonNull DataReceiver<Vehicle> lastUsedVehicleReceiver) {
    lastUsedVehicleReceiver.get()
        .doAfterTerminate(() -> loadLastUsedVehicle(lastUsedVehicleReceiver))
        .subscribe(
            vehicle -> lastUsedVehicle = vehicle,
            throwable -> {
            }
        );
  }

  @NonNull
  @Override
  public Completable loadVehiclesAndOptions() {
    return gateway.getExecutorVehicles()
        .map(list -> {
          vehiclesObserver.onNext(list);
          if (list.isEmpty()) {
            throw new NoVehiclesAvailableException();
          }
          int firstFreeIndex = -1;
          int freeVehiclesCount = 0;
          for (int i = list.size() - 1; i >= 0; i--) {
            if (!list.get(i).isBusy()) {
              if (lastUsedVehicle != null && list.get(i).getId() == lastUsedVehicle.getId()) {
                vehicleChoiceObserver.onNext(list.get(i));
                return list;
              }
              firstFreeIndex = i;
              freeVehiclesCount++;
            }
          }
          if (freeVehiclesCount == 0) {
            throw new NoFreeVehiclesException();
          }
          vehicleChoiceObserver.onNext(list.get(firstFreeIndex));
          return list;
        })
        .toCompletable()
        .andThen(
            gateway.getExecutorOptions()
                .map(list -> {
                  driverOptionsObserver.onNext(list);
                  return list;
                })
                .toCompletable()
        );
  }
}
