package com.fasten.executor_driver.backend.geolocation;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest.Builder;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;

/**
 * Подключается к Fused Location Api и подписывается на обновления геопозиции.
 */
public class GeolocationCenterImpl implements GeolocationCenter {

  @NonNull
  private final FusedLocationProviderClient mFusedLocationClient;
  @NonNull
  private final SettingsClient mSettingsClient;
  @NonNull
  private final LocationRequest mLocationRequest;

  // Создаем тут клиента для либы короче
  public GeolocationCenterImpl(Context context) {
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    mSettingsClient = LocationServices.getSettingsClient(context);
    mLocationRequest = new LocationRequest();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  // А тут короче плод двухдневных мозговых штурмов:
  // Замудренная склейка гугло АПИ и Rx.
  // Когда-нибудь задокументирую ¯\_(ツ)_/¯
  @Override
  public Flowable<Location> getLocations(long maxInterval) {
    GeoLocationCallback locationCallback = new GeoLocationCallback();
    return Flowable.<Location>create(emitter -> {
      // Begin by checking if the device has the necessary location settings.
      mSettingsClient.checkLocationSettings(
          new Builder().addLocationRequest(mLocationRequest.setInterval(maxInterval)).build()
      ).addOnSuccessListener(
          command -> new Thread(command).start(),
          locationSettingsResponse -> {
            try {
              mFusedLocationClient.requestLocationUpdates(
                  mLocationRequest, locationCallback.setEmitter(emitter), Looper.getMainLooper()
              );
            } catch (SecurityException se) {
              emitter.onError(se);
            }
          }
      ).addOnFailureListener(
          command -> new Thread(command).start(), throwable -> {
            int statusCode = ((ApiException) throwable).getStatusCode();
            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
              throwable = new GeoApiException(((ResolvableApiException) throwable).getResolution());
            }
            emitter.onError(throwable);
          }
      );
    }, BackpressureStrategy.BUFFER)
        .doOnCancel(() -> mFusedLocationClient.removeLocationUpdates(locationCallback)
            .addOnCompleteListener(command -> new Thread(command).start(), task -> {
            })
        );
  }

  // А это колбэк геопозиций с эмиттером.
  private class GeoLocationCallback extends LocationCallback {

    @Nullable
    private Emitter<Location> emitter;

    private GeoLocationCallback setEmitter(@Nullable Emitter<Location> emitter) {
      this.emitter = emitter;
      return this;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
      super.onLocationResult(locationResult);
      for (Location location : locationResult.getLocations()) {
        if (emitter != null) {
          emitter.onNext(location);
        }
      }
    }
  }
}
