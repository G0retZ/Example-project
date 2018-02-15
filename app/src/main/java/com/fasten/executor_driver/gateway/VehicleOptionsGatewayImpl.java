package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.outgoing.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import io.reactivex.Completable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VehicleOptionsGatewayImpl implements VehicleOptionsGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public VehicleOptionsGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Completable sendVehicleOptions(@NonNull Vehicle vehicle) {
    return api.selectCarWithOptions(vehicle.getId(), map(vehicle.getVehicleOptions()));
  }

  private List<ApiVehicleOptionItem> map(List<VehicleOption> vehicleOptions) {
    ArrayList<ApiVehicleOptionItem> apiVehicleOptionItems = new ArrayList<>();
    for (VehicleOption vehicleOption : vehicleOptions) {
      apiVehicleOptionItems.add(
          new ApiVehicleOptionItem(vehicleOption.getId(), vehicleOption.getValue().toString())
      );
    }
    return apiVehicleOptionItems;
  }
}
