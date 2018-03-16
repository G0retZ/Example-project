package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.geolocation.GeolocationCenter;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.interactor.GeoLocationGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GeoLocationGatewayImpl implements GeoLocationGateway {

  @NonNull
  private final GeolocationCenter geolocationCenter;

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
