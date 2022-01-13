package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.interactor.OrderRouteGateway;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class OrderRouteGatewayImpl implements OrderRouteGateway {

  @NonNull
  private final ApiService apiService;
  @NonNull
  private final Mapper<ApiRoutePoint, RoutePoint> mapper;

  @Inject
  public OrderRouteGatewayImpl(@NonNull ApiService apiService,
      @NonNull Mapper<ApiRoutePoint, RoutePoint> mapper) {
    this.apiService = apiService;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Single<List<RoutePoint>> closeRoutePoint(@NonNull RoutePoint routePoint) {
    return apiService.completeRoutePoint(routePoint.getId()).subscribeOn(Schedulers.io())
        .flatMapObservable(listApiSimpleResult -> {
          List<ApiRoutePoint> data = listApiSimpleResult.getData();
          if (data == null) {
              throw new DataMappingException("No route!");
          }
          return Observable.fromIterable(data);
        })
        .map(mapper::map)
        .toList();
  }

  @NonNull
  @Override
  public Single<List<RoutePoint>> nextRoutePoint(@NonNull RoutePoint routePoint) {
    return apiService.makeRoutePointNext(routePoint.getId()).subscribeOn(Schedulers.io())
        .flatMapObservable(listApiSimpleResult -> {
          List<ApiRoutePoint> data = listApiSimpleResult.getData();
          if (data == null) {
            throw new DataMappingException("No route!");
          }
          return Observable.fromIterable(data);
        })
        .map(mapper::map)
        .toList();
  }
}
