package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class GeoLocationAvailabilityGatewayImpl implements CommonGateway<Boolean> {

  @NonNull
  private final GeolocationCenter geolocationCenter;

  @Inject
  public GeoLocationAvailabilityGatewayImpl(@NonNull GeolocationCenter geolocationCenter) {
    this.geolocationCenter = geolocationCenter;
  }

  @NonNull
  @Override
  public Flowable<Boolean> getData() {
    return geolocationCenter.getLocationsAvailability().subscribeOn(Schedulers.io());
  }
}
