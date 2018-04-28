package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.outgoing.ApiOptionItem;
import com.fasten.executor_driver.backend.web.outgoing.ApiOptionItems;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class CurrentVehicleOptionsGatewayImpl implements VehicleOptionsGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public CurrentVehicleOptionsGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Completable sendVehicleOptions(@NonNull Vehicle vehicle,
      @NonNull List<Option> driverOptions) {
    return api.setSelectedVehicleOptions(map(vehicle.getOptions(), driverOptions))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  private ApiOptionItems map(List<Option> vehicleOptions, List<Option> driverOptions) {
    ArrayList<ApiOptionItem> apiOptionItems1 = new ArrayList<>();
    ArrayList<ApiOptionItem> apiOptionItems2 = new ArrayList<>();
    for (Option option : vehicleOptions) {
      apiOptionItems1.add(
          new ApiOptionItem(option.getId(), option.getValue().toString())
      );
    }
    for (Option option : driverOptions) {
      apiOptionItems2.add(
          new ApiOptionItem(option.getId(), option.getValue().toString())
      );
    }
    return new ApiOptionItems(apiOptionItems1, apiOptionItems2);
  }
}
