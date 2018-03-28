package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.backend.websocket.outgoing.ApiGeoLocation;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.interactor.GeoTrackingGateway;
import com.google.gson.Gson;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.client.StompClient;

public class GeoTrackingGatewayImpl implements GeoTrackingGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Gson gson;

  public GeoTrackingGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
    gson = new Gson();
  }

  @NonNull
  @Override
  public Completable sendGeoLocation(GeoLocation geoLocation) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(BuildConfig.GOLOCATION_DESTINATION, gson.toJson(
          new ApiGeoLocation(
              geoLocation.getLatitude(), geoLocation.getLongitude(), geoLocation.getTimestamp()
          )
      ))
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
