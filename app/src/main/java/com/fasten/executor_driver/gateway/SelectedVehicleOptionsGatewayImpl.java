package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.backend.web.incoming.ApiSelectedOptionsForOnline;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleOptionsGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class SelectedVehicleOptionsGatewayImpl implements SelectedVehicleOptionsGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<ApiOptionItem, Option> optionMapper;
  @NonNull
  private final Mapper<Throwable, Throwable> errorMapper;
  @Nullable
  private Single<ApiSelectedOptionsForOnline> apiOptionsForOnlineSingle;

  @Inject
  public SelectedVehicleOptionsGatewayImpl(@NonNull ApiService api,
      @NonNull Mapper<ApiOptionItem, Option> optionMapper,
      @NonNull Mapper<Throwable, Throwable> errorMapper) {
    this.api = api;
    this.optionMapper = optionMapper;
    this.errorMapper = errorMapper;
  }

  @NonNull
  @Override
  public Single<List<Option>> getVehicleOptions() {
    if (apiOptionsForOnlineSingle == null) {
      apiOptionsForOnlineSingle = api.getSelectedOptionsForOnline()
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single())
          .doOnError(throwable -> apiOptionsForOnlineSingle = null)
          .cache();
    }
    return apiOptionsForOnlineSingle
        .flattenAsObservable(ApiSelectedOptionsForOnline::getVehicleOptions)
        .map(optionMapper::map)
        .toList()
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }

  @NonNull
  @Override
  public Single<List<Option>> getExecutorOptions() {
    if (apiOptionsForOnlineSingle == null) {
      apiOptionsForOnlineSingle = api.getSelectedOptionsForOnline()
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single())
          .doOnError(throwable -> apiOptionsForOnlineSingle = null)
          .cache();
    }
    return apiOptionsForOnlineSingle
        .flattenAsObservable(ApiSelectedOptionsForOnline::getDriverOptions)
        .map(optionMapper::map)
        .toList()
        .onErrorResumeNext(e -> Single.error(errorMapper.map(e)));
  }
}
