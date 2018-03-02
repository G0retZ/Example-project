package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.incoming.ApiServiceItem;
import com.fasten.executor_driver.entity.Service;
import com.fasten.executor_driver.interactor.services.ServicesGateway;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class ServicesGatewayImpl implements ServicesGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<ApiServiceItem, Service> serviceMapper;

  @Inject
  ServicesGatewayImpl(@NonNull ApiService api,
      @Named("serviceMapper") @NonNull Mapper<ApiServiceItem, Service> serviceMapper) {
    this.api = api;
    this.serviceMapper = serviceMapper;
  }

  @Override
  public Single<List<Service>> getServices() {
    return api.getMyServices()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(this::mapServices);
  }

  @NonNull
  @Override
  public Completable sendSelectedServices(@NonNull List<Service> services) {
    StringBuilder servicesIds = new StringBuilder();
    for (Service service : services) {
      if (service.getValue()) {
        servicesIds.append("").append(service.getId()).append(",");
      }
    }
    return api.setMyServices(servicesIds.subSequence(0, servicesIds.length() - 1).toString())
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  private List<Service> mapServices(List<ApiServiceItem> apiServiceItems) throws Exception {
    ArrayList<Service> services = new ArrayList<>();
    for (ApiServiceItem apiServiceItem : apiServiceItems) {
      services.add(serviceMapper.map(apiServiceItem));
    }
    return services;
  }
}
