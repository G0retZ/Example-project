package com.cargopull.executor_driver.interactor.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class HeatMapUseCaseImpl implements HeatMapUseCase {

  private final static int POLLING_INTERVAL = 5;

  @NonNull
  private final HeatMapGateway gateway;
  @Nullable
  private Flowable<String> heatMapEmitter;

  @Inject
  public HeatMapUseCaseImpl(@NonNull HeatMapGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> loadHeatMap() {
    if (heatMapEmitter == null) {
      heatMapEmitter = gateway.getHeatMap()
          .observeOn(Schedulers.single())
          .repeatWhen(completed -> completed
              .concatMap(v -> Flowable.timer(POLLING_INTERVAL, TimeUnit.MINUTES)))
          .retryWhen(failed -> failed
              .concatMap(throwable -> Flowable.timer(POLLING_INTERVAL, TimeUnit.MINUTES)))
          .share();
    }
    return heatMapEmitter;
  }
}
