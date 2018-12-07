package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersSummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.interactor.OrdersHistorySummaryGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Map;
import javax.inject.Inject;

public class OrdersHistorySummaryGatewayImpl implements OrdersHistorySummaryGateway {

  @NonNull
  private final ApiService api;
  @NonNull
  private final Mapper<Map<String, ApiOrdersSummary>, OrdersHistorySummary> mapper;

  @Inject
  public OrdersHistorySummaryGatewayImpl(@NonNull ApiService api,
      @NonNull Mapper<Map<String, ApiOrdersSummary>, OrdersHistorySummary> mapper) {
    this.api = api;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Single<OrdersHistorySummary> getOrdersHistorySummary(long fromDate, long toDate) {
    return api.getOrdersHistory(fromDate, toDate)
        .subscribeOn(Schedulers.io())
        .map(mapper::map);
  }
}
