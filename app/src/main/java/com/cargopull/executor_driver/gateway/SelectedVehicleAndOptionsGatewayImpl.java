package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionsForOnline;
import com.cargopull.executor_driver.backend.web.incoming.ApiVehicle;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class SelectedVehicleAndOptionsGatewayImpl implements VehiclesAndOptionsGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<ApiOptionItem, Option> optionMapper;
  @NonNull
  private final Mapper<ApiVehicle, Vehicle> vehicleMapper;
  @NonNull
  private final Mapper<Throwable, Throwable> errorMapper;

  @Inject
  public SelectedVehicleAndOptionsGatewayImpl(@NonNull ApiService api,
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
    return api.getSelectedOptionsForOnline()
        .subscribeOn(Schedulers.io())
        .flattenAsObservable(ApiOptionsForOnline::getCars)
        .map(vehicleMapper::map)
        .toList()
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }

  @NonNull
  @Override
  public Single<List<Option>> getExecutorOptions() {
    return api.getSelectedOptionsForOnline()
        .subscribeOn(Schedulers.io())
        .flattenAsObservable(ApiOptionsForOnline::getDriverOptions)
        .map(optionMapper::map)
        .toList()
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }
}
