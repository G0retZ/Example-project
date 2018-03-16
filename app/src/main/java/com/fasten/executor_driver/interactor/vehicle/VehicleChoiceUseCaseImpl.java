package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import java.util.List;
import javax.inject.Inject;

public class VehicleChoiceUseCaseImpl implements VehicleChoiceUseCase {

  @NonNull
  private final DataReceiver<List<Vehicle>> vehiclesReceiver;
  @NonNull
  private final Observer<Vehicle> vehicleChoiceObserver;
  @Nullable
  private List<Vehicle> vehicles;

  @Inject
  public VehicleChoiceUseCaseImpl(
      @NonNull DataReceiver<List<Vehicle>> vehiclesReceiver,
      @NonNull Observer<Vehicle> vehicleChoiceObserver) {
    this.vehiclesReceiver = vehiclesReceiver;
    this.vehicleChoiceObserver = vehicleChoiceObserver;
  }

  @NonNull
  @Override
  public Observable<List<Vehicle>> getVehicles() {
    return vehiclesReceiver.get()
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
      vehicleChoiceObserver.onNext(vehicle);
      return null;
    });
  }
}
