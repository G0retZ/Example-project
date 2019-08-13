package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.AppConfigKt;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.backend.web.outgoing.ApiGeoLocation;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.interactor.GeoTrackingGateway;
import com.google.gson.Gson;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class GeoTrackingGatewayImpl implements GeoTrackingGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Gson gson;

  @Inject
  public GeoTrackingGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
    gson = new Gson();
  }

  @NonNull
  @Override
  public Completable sendGeoLocation(GeoLocation geoLocation) {
    return stompClient.send(AppConfigKt.GEOLOCATION_DESTINATION, gson.toJson(
        new ApiGeoLocation(
            geoLocation.getLatitude(), geoLocation.getLongitude(), geoLocation.getTimestamp()
        )
    ))
        .subscribeOn(Schedulers.io());
  }
}
