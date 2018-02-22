package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehicleOptionsUseCaseImpl implements VehicleOptionsUseCase {

  @NonNull
  private final VehicleOptionsGateway gateway;
  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;
  @NonNull
  private final DataSharer<Vehicle> lastUsedVehicleSharer;
  @Nullable
  private Vehicle vehicle;

  @Inject
  VehicleOptionsUseCaseImpl(
      @NonNull VehicleOptionsGateway gateway,
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer,
      @Named("lastUsedVehicleSharer") @NonNull DataSharer<Vehicle> lastUsedVehicleSharer) {
    this.gateway = gateway;
    this.vehicleChoiceSharer = vehicleChoiceSharer;
    this.lastUsedVehicleSharer = lastUsedVehicleSharer;
  }

  @NonNull
  @Override
  public Observable<List<VehicleOption>> getVehicleOptions() {
    return vehicleChoiceSharer.get()
        .concatMap(vehicle -> {
          this.vehicle = vehicle;
          return Observable.fromIterable(vehicle.getVehicleOptions())
              .filter(VehicleOption::isVariable)
              .toList()
              .toObservable();
        });
  }

  @Override
  public Completable setSelectedVehicleOptions(List<VehicleOption> vehicleOptions) {
    if (vehicle == null) {
      return Completable.error(new DataMappingException());
    }
    vehicle.setVehicleOptions(vehicleOptions.toArray(new VehicleOption[vehicleOptions.size()]));
    return gateway.sendVehicleOptions(vehicle)
        .doOnComplete(() -> lastUsedVehicleSharer.share(vehicle));
  }
}
