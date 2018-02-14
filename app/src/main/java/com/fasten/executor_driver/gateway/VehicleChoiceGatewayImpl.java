package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class VehicleChoiceGatewayImpl implements VehicleChoiceGateway {

  private final ApiService api;
  private final Mapper<ApiVehicle, Vehicle> mapper;

  @Inject
  public VehicleChoiceGatewayImpl(ApiService api,
      @Named("vehicleMapper") Mapper<ApiVehicle, Vehicle> mapper) {
    this.api = api;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Single<List<Vehicle>> getExecutorVehicles() {
    return api.getCars()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(this::map);
  }

  private List<Vehicle> map(List<ApiVehicle> apiVehicles) throws Exception {
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    for (ApiVehicle apiVehicle : apiVehicles) {
      vehicles.add(mapper.map(apiVehicle));
    }
    return vehicles;
  }
}
