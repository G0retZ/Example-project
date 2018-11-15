package com.cargopull.executor_driver.interactor.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class HeatMapUseCaseImpl implements HeatMapUseCase {

  @NonNull
  private final HeatMapGateway gateway;
  @Nullable
  private Flowable<String> heatMapFlowable;

  @Inject
  public HeatMapUseCaseImpl(@NonNull HeatMapGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> loadHeatMap() {
    if (heatMapFlowable == null) {
      heatMapFlowable = gateway.getHeatMap()
          .observeOn(Schedulers.single())
          .retry(3)
          .toFlowable()
          .concatWith(Flowable.never())
          .replay(1)
          .refCount();
    }
    return heatMapFlowable;
  }
}
