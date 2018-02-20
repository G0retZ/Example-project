package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
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

  @Inject
  VehiclesUseCaseImpl(@NonNull VehiclesGateway gateway,
      @Named("vehiclesSharer") @NonNull DataSharer<List<Vehicle>> vehiclesSharer,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer) {
    this.gateway = gateway;
    this.vehiclesSharer = vehiclesSharer;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
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
          int lastFreeIndex = -1;
          int freeVehiclesCount = 0;
          for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isBusy()) {
              lastFreeIndex = i;
              freeVehiclesCount++;
            }
          }
          if (freeVehiclesCount == 0) {
            throw new NoFreeVehiclesException();
          } else if (freeVehiclesCount == 1) {
            vehicleChoiceSharer.share(list.get(lastFreeIndex));
            throw new OnlyOneVehicleAvailableException();
          }
          return list;
        })
        .toCompletable();
  }
}
