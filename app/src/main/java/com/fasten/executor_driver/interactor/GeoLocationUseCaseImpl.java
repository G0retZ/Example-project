package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class GeoLocationUseCaseImpl implements GeoLocationUseCase {

  @NonNull
  private final GeoLocationGateway gateway;
  @NonNull
  private final DataReceiver<ExecutorState> executorStateReceiver;
  @NonNull
  private final Observer<GeoLocation> geoLocationObserver;

  @Nullable
  private Disposable executorStateDisposable;

  @Nullable
  private Disposable geoLocationDisposable;

  public GeoLocationUseCaseImpl(@NonNull GeoLocationGateway gateway,
      @NonNull DataReceiver<ExecutorState> executorStateReceiver,
      @NonNull Observer<GeoLocation> geoLocationObserver) {
    this.gateway = gateway;
    this.executorStateReceiver = executorStateReceiver;
    this.geoLocationObserver = geoLocationObserver;
  }

  public Completable reload() {
    return Completable.fromCallable(() -> {
      if (executorStateDisposable != null && !executorStateDisposable.isDisposed()) {
        executorStateDisposable.dispose();
      }
      executorStateDisposable = executorStateReceiver.get()
          .subscribe(this::consumeExecutorState, throwable -> {
          });
      return 0;
    });
  }

  private void consumeExecutorState(@NonNull ExecutorState executorState) {
    switch (executorState) {
      case UNAUTHORIZED:
        if (geoLocationDisposable != null && !geoLocationDisposable.isDisposed()) {
          geoLocationDisposable.dispose();
        }
        break;
      case SHIFT_CLOSED:
        if (geoLocationDisposable != null && !geoLocationDisposable.isDisposed()) {
          geoLocationDisposable.dispose();
        }
        geoLocationDisposable = gateway.getGeoLocations(3600000)
            .subscribe(geoLocationObserver::onNext, geoLocationObserver::onError);
        break;
      case SHIFT_OPENED:
        if (geoLocationDisposable != null && !geoLocationDisposable.isDisposed()) {
          geoLocationDisposable.dispose();
        }
        geoLocationDisposable = gateway.getGeoLocations(180000)
            .subscribe(geoLocationObserver::onNext, geoLocationObserver::onError);
        break;
      case ONLINE:
        if (geoLocationDisposable != null && !geoLocationDisposable.isDisposed()) {
          geoLocationDisposable.dispose();
        }
        geoLocationDisposable = gateway.getGeoLocations(15000)
            .subscribe(geoLocationObserver::onNext, geoLocationObserver::onError);
        break;
    }
  }
}
