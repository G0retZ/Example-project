package com.cargopull.executor_driver.backend.geolocation;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import javax.inject.Inject;

/**
 * Подключается к Fused Location Api и подписывается на обновления геопозиции.
 */
public class GeolocationCenterImpl implements GeolocationCenter {

  @NonNull
  private final FusedLocationProviderClient mFusedLocationClient;
  @NonNull
  private final LocationRequest mLocationRequest;
  @NonNull
  private final Context appContext;

  // Создаем тут клиента для либы короче
  @Inject
  public GeolocationCenterImpl(Context context) {
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    appContext = context.getApplicationContext();
  }

  // А тут короче плод двухдневных мозговых штурмов:
  // Замудренная склейка гугло АПИ и Rx.
  // Когда-нибудь задокументирую ¯\_(ツ)_/¯
  @Override
  public Flowable<Location> getLocations(long maxInterval) {
    GeoLocationCallback locationCallback = new GeoLocationCallback();
    return Flowable.<Location>create(emitter -> {
      // Begin by checking if the device has the necessary location settings.
      if (ActivityCompat.checkSelfPermission(appContext, permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED
          ||
          ActivityCompat.checkSelfPermission(appContext, permission.ACCESS_COARSE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
        emitter.onError(new SecurityException());
        return;
      }
      mFusedLocationClient.requestLocationUpdates(mLocationRequest.setInterval(maxInterval),
          locationCallback.setEmitter(emitter), Looper.getMainLooper());
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
