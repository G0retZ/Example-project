package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehicleChoiceUseCaseImpl implements VehicleChoiceUseCase {

  @NonNull
  private final DataSharer<List<Vehicle>> vehiclesSharer;
  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;
  @Nullable
  private List<Vehicle> vehicles;

  @Inject
  VehicleChoiceUseCaseImpl(
      @Named("vehiclesSharer") @NonNull DataSharer<List<Vehicle>> vehiclesSharer,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer) {
    this.vehiclesSharer = vehiclesSharer;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
  }

  @NonNull
  @Override
  public Observable<List<Vehicle>> getVehicles() {
    return vehiclesSharer.get()
        .map(list -> {
          if (list.isEmpty()) {
            throw new NoVehiclesAvailableException();
          }
          vehicles = list;
          return list;
        });
  }

  @Override
  public Completable selectVehicle(Vehicle vehicle) {
    return Completable.fromCallable(() -> {
      if (vehicles == null || !vehicles.contains(vehicle)) {
        throw new IndexOutOfBoundsException();
      }
      if (vehicle.isBusy()) {
        throw new IllegalArgumentException();
      }
      vehicleChoiceSharer.share(vehicle);
      return null;
    });
  }
}
