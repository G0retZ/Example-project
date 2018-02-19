package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoVehicleOptionsAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;

public class VehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final VehicleOptionsGateway gateway;
  @NonNull
  private final DataSharer<Vehicle> vehicleSharer;
  @Nullable
  private Vehicle vehicle;

  VehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @NonNull DataSharer<Vehicle> vehicleSharer) {
    this.gateway = gateway;
    this.vehicleSharer = vehicleSharer;
  }

  @NonNull
  @Override
  public Observable<List<VehicleOption>> getVehicleOptions() {
    return vehicleSharer.get()
        .concatMap(vehicle -> {
          this.vehicle = vehicle;
          return Observable.fromIterable(vehicle.getVehicleOptions())
              .filter(VehicleOption::isVariable)
              .toList()
              .toObservable()
              .map(list -> {
                if (list.isEmpty()) {
                  throw new NoVehicleOptionsAvailableException();
                } else {
                  return list;
                }
              });
        });
  }

  @Override
  public Completable setSelectedVehicleOptions(List<VehicleOption> vehicleOptions) {
    if (vehicle == null) {
      return Completable.error(new DataMappingException());
    }
    vehicle.setVehicleOptions(vehicleOptions.toArray(new VehicleOption[vehicleOptions.size()]));
    return gateway.sendVehicleOptions(vehicle);
  }
}
