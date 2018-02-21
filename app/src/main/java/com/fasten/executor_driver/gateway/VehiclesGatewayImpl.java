package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehiclesGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehiclesGatewayImpl implements VehiclesGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<ApiVehicle, Vehicle> vehicleMapper;
  @NonNull
  private final Mapper<Throwable, Throwable> errorMapper;

  @Inject
  public VehiclesGatewayImpl(@NonNull ApiService api,
      @Named("vehicleMapper") @NonNull Mapper<ApiVehicle, Vehicle> vehicleMapper,
      @Named("errorMapper") @NonNull Mapper<Throwable, Throwable> errorMapper) {
    this.api = api;
    this.vehicleMapper = vehicleMapper;
    this.errorMapper = errorMapper;
  }

  @NonNull
  @Override
  public Single<List<Vehicle>> getExecutorVehicles() {
    return api.getCars()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(this::map)
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }

  private List<Vehicle> map(List<ApiVehicle> apiVehicles) throws Exception {
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    for (ApiVehicle apiVehicle : apiVehicles) {
      vehicles.add(vehicleMapper.map(apiVehicle));
    }
    return vehicles;
  }
}
