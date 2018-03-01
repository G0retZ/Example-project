package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.outgoing.ApiOptionItem;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
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
  public Completable sendVehicleOptions(@NonNull Vehicle vehicle,
      @NonNull List<Option> driverOptions) {
    return api.occupyCarWithOptions(vehicle.getId(), map(vehicle.getOptions()))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  private List<ApiOptionItem> map(List<Option> options) {
    ArrayList<ApiOptionItem> apiOptionItems = new ArrayList<>();
    for (Option option : options) {
      apiOptionItems.add(
          new ApiOptionItem(option.getId(), option.getValue().toString())
      );
    }
    return apiOptionItems;
  }
}
