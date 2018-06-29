package com.fasten.executor_driver.interactor.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Flowable;
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
          .repeatWhen(completed -> completed
              .concatMap(v -> Flowable.timer(POLLING_INTERVAL, TimeUnit.MINUTES)))
          .retryWhen(failed -> failed
              .concatMap(throwable -> Flowable.timer(POLLING_INTERVAL, TimeUnit.MINUTES)))
          .share();
    }
    return heatMapEmitter;
  }
}
