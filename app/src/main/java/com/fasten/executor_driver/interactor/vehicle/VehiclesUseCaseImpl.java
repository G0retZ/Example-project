package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.OnlyOneVehicleAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehiclesUseCaseImpl implements VehiclesUseCase {

  @NonNull
  private final VehiclesGateway gateway;
  @NonNull
  private final DataSharer<List<Vehicle>> vehiclesSharer;
  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;
  @Nullable
  private Vehicle lastUsedVehicle;

  @Inject
  VehiclesUseCaseImpl(@NonNull VehiclesGateway gateway,
      @Named("vehiclesSharer") @NonNull DataSharer<List<Vehicle>> vehiclesSharer,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer,
      @Named("lastUsedVehicleSharer") @NonNull DataSharer<Vehicle> lastUsedVehicleSharer) {
    this.gateway = gateway;
    this.vehiclesSharer = vehiclesSharer;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
    lastUsedVehicleSharer.get().subscribe(vehicle -> lastUsedVehicle = vehicle);
  }

  @NonNull
  @Override
  public Completable loadVehicles() {
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
          } else if (freeVehiclesCount == 1) {
            vehicleChoiceSharer.share(list.get(firstFreeIndex));
            throw new OnlyOneVehicleAvailableException();
          } else {
            vehicleChoiceSharer.share(list.get(firstFreeIndex));
          }

          return list;
        })
        .toCompletable();
  }
}
