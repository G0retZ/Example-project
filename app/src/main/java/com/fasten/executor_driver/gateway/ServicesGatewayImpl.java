package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.incoming.ApiServiceItem;
import com.fasten.executor_driver.entity.Service;
import com.fasten.executor_driver.interactor.services.ServicesGateway;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class ServicesGatewayImpl implements ServicesGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<ApiServiceItem, Service> serviceMapper;

  @Inject
  public ServicesGatewayImpl(@NonNull ApiService api,
      @NonNull Mapper<ApiServiceItem, Service> serviceMapper) {
    this.api = api;
    this.serviceMapper = serviceMapper;
  }

  @Override
  public Single<List<Service>> getServices() {
    return api.getMyServices()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .flattenAsObservable(apiServiceItems -> apiServiceItems)
        .map(serviceMapper::map)
        .toList();
  }

  @NonNull
  @Override
  public Completable sendSelectedServices(@NonNull List<Service> services) {
    StringBuilder servicesIds = new StringBuilder();
    for (Service service : services) {
      servicesIds.append("").append(service.getId()).append(",");
    }
    return api.setMyServices(servicesIds.subSequence(0, servicesIds.length() - 1).toString())
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
