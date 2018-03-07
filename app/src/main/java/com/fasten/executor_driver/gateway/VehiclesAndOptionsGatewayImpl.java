package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionsForOnline;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VehiclesAndOptionsGatewayImpl implements VehiclesAndOptionsGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<ApiOptionItem, Option> optionMapper;
  @NonNull
  private final Mapper<ApiVehicle, Vehicle> vehicleMapper;
  @NonNull
  private final Mapper<Throwable, Throwable> errorMapper;
  @Nullable
  private List<Vehicle> vehicles;
  @Nullable
  private List<Option> driverOptions;

  @Inject
  public VehiclesAndOptionsGatewayImpl(@NonNull ApiService api,
      @NonNull Mapper<ApiOptionItem, Option> optionMapper,
      @NonNull Mapper<ApiVehicle, Vehicle> vehicleMapper,
      @NonNull Mapper<Throwable, Throwable> errorMapper) {
    this.api = api;
    this.optionMapper = optionMapper;
    this.vehicleMapper = vehicleMapper;
    this.errorMapper = errorMapper;
  }

  @NonNull
  @Override
  public Single<List<Vehicle>> getExecutorVehicles() {
    if (vehicles != null) {
      return Single.just(vehicles);
    }
    return api.getOptionsForOnline()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(this::mapVehicles)
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }

  @NonNull
  @Override
  public Single<List<Option>> getExecutorOptions() {
    if (driverOptions != null) {
      return Single.just(driverOptions);
    }
    return api.getOptionsForOnline()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(this::mapDriverOptions)
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }

  private List<Vehicle> mapVehicles(ApiOptionsForOnline apiOptionsForOnline) throws Exception {
    driverOptions = new ArrayList<>();
    for (ApiOptionItem apiOptionItem : apiOptionsForOnline.getDriverOptions()) {
      driverOptions.add(optionMapper.map(apiOptionItem));
    }
    vehicles = new ArrayList<>();
    for (ApiVehicle apiVehicle : apiOptionsForOnline.getCars()) {
      vehicles.add(vehicleMapper.map(apiVehicle));
    }
    return vehicles;
  }

  private List<Option> mapDriverOptions(ApiOptionsForOnline apiOptionsForOnline) throws Exception {
    vehicles = new ArrayList<>();
    for (ApiVehicle apiVehicle : apiOptionsForOnline.getCars()) {
      vehicles.add(vehicleMapper.map(apiVehicle));
    }
    driverOptions = new ArrayList<>();
    for (ApiOptionItem apiOptionItem : apiOptionsForOnline.getDriverOptions()) {
      driverOptions.add(optionMapper.map(apiOptionItem));
    }
    return driverOptions;
  }
}
