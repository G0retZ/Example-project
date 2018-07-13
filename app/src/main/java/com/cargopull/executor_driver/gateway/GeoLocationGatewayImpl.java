package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.interactor.GeoLocationGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class GeoLocationGatewayImpl implements GeoLocationGateway {

  @NonNull
  private final GeolocationCenter geolocationCenter;

  @Inject
  public GeoLocationGatewayImpl(@NonNull GeolocationCenter geolocationCenter) {
    this.geolocationCenter = geolocationCenter;
  }

  @NonNull
  @Override
  public Flowable<GeoLocation> getGeoLocations(long interval) {
    return geolocationCenter.getLocations(interval)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(location ->
            new GeoLocation(location.getLatitude(), location.getLongitude(), location.getTime()));
  }
}
