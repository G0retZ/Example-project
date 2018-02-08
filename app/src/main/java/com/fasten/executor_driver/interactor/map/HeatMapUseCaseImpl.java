package com.fasten.executor_driver.interactor.map;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class HeatMapUseCaseImpl implements HeatMapUseCase {

  private final static int POLLING_INTERVAL = 5;

  private final HeatMapGateway gateway;
  private Flowable<String> heatMapEmitter;

  @Inject
  HeatMapUseCaseImpl(HeatMapGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> loadHeatMap() {
    if (heatMapEmitter == null) {
      heatMapEmitter = gateway.getHeatMap()
          .repeatWhen(completed -> completed
              .concatMap(v -> Flowable.timer(POLLING_INTERVAL, TimeUnit.SECONDS, Schedulers.io())))
          .retryWhen(failed -> failed
              .concatMap(v -> Flowable.timer(POLLING_INTERVAL, TimeUnit.SECONDS, Schedulers.io())))
          .share();
    }
    return heatMapEmitter;
  }
}
