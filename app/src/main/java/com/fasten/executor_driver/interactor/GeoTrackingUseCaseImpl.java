package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

class GeoTrackingUseCaseImpl implements GeoTrackingUseCase {

  @NonNull
  private final GeoTrackingGateway gateway;
  @NonNull
  private final DataReceiver<GeoLocation> geoLocationReceiver;
  @Nullable
  private Disposable disposable;

  GeoTrackingUseCaseImpl(@NonNull GeoTrackingGateway gateway,
      @NonNull DataReceiver<GeoLocation> geoLocationReceiver) {
    this.gateway = gateway;
    this.geoLocationReceiver = geoLocationReceiver;
  }

  @Override
  public Completable reload() {
    return Completable.fromCallable(() -> {
      getGeoLocations();
      return 0;
    });
  }

  private void getGeoLocations() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = geoLocationReceiver.get()
          .doAfterTerminate(this::getGeoLocations)
          .subscribe(
              geoLocation -> gateway.sendGeoLocation(geoLocation).subscribe(() -> {
              }, throwable -> {
              }),
              throwable -> {
              }
          );
    }
  }
}
