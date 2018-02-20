package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.OnlyOneVehicleAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehicleChoiceUseCaseImpl implements VehicleChoiceUseCase {

  @NonNull
  private final VehiclesGateway vehiclesGateway;
  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;
  @Nullable
  private List<Vehicle> vehicles;

  @Inject
  VehicleChoiceUseCaseImpl(
      @NonNull VehiclesGateway vehiclesGateway,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer) {
    this.vehiclesGateway = vehiclesGateway;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
  }

  @NonNull
  @Override
  public Single<List<Vehicle>> getVehicles() {
    return vehiclesGateway.getExecutorVehicles()
        .map(list -> {
          if (list.isEmpty()) {
            throw new NoVehiclesAvailableException();
          }
          if (list.size() == 1 && !list.get(0).isBusy()) {
            vehicleChoiceSharer.share(list.get(0));
            throw new OnlyOneVehicleAvailableException();
          }
          vehicles = list;
          return list;
        });
  }

  @Override
  public Completable setSelectedVehicle(int index) {
    return Completable.fromCallable(() -> {
      if (vehicles == null) {
        throw new IndexOutOfBoundsException();
      }
      if (vehicles.get(index).isBusy()) {
        throw new IndexOutOfBoundsException();
      }
      vehicleChoiceSharer.share(vehicles.get(index));
      return null;
    });
  }
}
