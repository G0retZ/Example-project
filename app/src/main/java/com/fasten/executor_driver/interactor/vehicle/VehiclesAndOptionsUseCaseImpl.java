package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehiclesAndOptionsUseCaseImpl implements VehiclesAndOptionsUseCase {

  @NonNull
  private final VehiclesAndOptionsGateway gateway;
  @NonNull
  private final DataSharer<List<Vehicle>> vehiclesSharer;
  @NonNull
  private final DataSharer<List<Option>> driverOptionsSharer;
  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;
  @Nullable
  private Vehicle lastUsedVehicle;

  @Inject
  VehiclesAndOptionsUseCaseImpl(@NonNull VehiclesAndOptionsGateway gateway,
      @Named("vehiclesSharer") @NonNull DataSharer<List<Vehicle>> vehiclesSharer,
      @Named("driverOptionsSharer") @NonNull DataSharer<List<Option>> driverOptionsSharer,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer,
      @Named("lastUsedVehicleSharer") @NonNull DataSharer<Vehicle> lastUsedVehicleSharer) {
    this.gateway = gateway;
    this.vehiclesSharer = vehiclesSharer;
    this.driverOptionsSharer = driverOptionsSharer;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
    lastUsedVehicleSharer.get().subscribe(
        vehicle -> lastUsedVehicle = vehicle,
        Throwable::printStackTrace
    );
  }

  @NonNull
  @Override
  public Completable loadVehiclesAndOptions() {
    return gateway.getExecutorVehicles()
        .map(list -> {
          vehiclesSharer.share(list);
          if (list.isEmpty()) {
            throw new NoVehiclesAvailableException();
          }
          int firstFreeIndex = -1;
          int freeVehiclesCount = 0;
          for (int i = list.size() - 1; i >= 0; i--) {
            if (!list.get(i).isBusy()) {
              if (lastUsedVehicle != null && list.get(i).getId() == lastUsedVehicle.getId()) {
                vehicleChoiceSharer.share(list.get(i));
                return list;
              }
              firstFreeIndex = i;
              freeVehiclesCount++;
            }
          }
          if (freeVehiclesCount == 0) {
            throw new NoFreeVehiclesException();
          }
          vehicleChoiceSharer.share(list.get(firstFreeIndex));
          return list;
        })
        .toCompletable()
        .andThen(
            gateway.getExecutorOptions()
                .map(list -> {
                  driverOptionsSharer.share(list);
                  return list;
                })
                .toCompletable()
        );
  }
}
