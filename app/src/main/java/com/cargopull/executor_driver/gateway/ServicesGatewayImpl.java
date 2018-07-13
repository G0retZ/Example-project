package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiServiceItem;
import com.cargopull.executor_driver.entity.Service;
import com.cargopull.executor_driver.interactor.services.ServicesGateway;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
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
    return api.getMySelectedServices()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .flatMapObservable(selected -> {
          List<String> selectedIds = Arrays.asList(selected.split(","));
          return api.getMyServices()
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.single())
              .flattenAsObservable(apiServiceItems -> apiServiceItems)
              .map(apiServiceItem -> {
                if (selectedIds.contains(String.valueOf(apiServiceItem.getId()))) {
                  apiServiceItem.setSelected(true);
                }
                return apiServiceItem;
              });
        })
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
