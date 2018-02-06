package com.fasten.executor_driver.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import io.reactivex.disposables.Disposable;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
    OnMyLocationButtonClickListener {

  private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION};

  @Nullable
  private PermissionChecker permissionChecker;
  @Nullable
  private Disposable permissionDisposable;

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    getMapAsync(this);
  }

  private GoogleMap googleMap;

  private Activity activity;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    this.activity = activity;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
    try {
      googleMap
          .setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.mapstyle_aubergine));
    } catch (Resources.NotFoundException e) {
      e.printStackTrace();
    }
    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    googleMap.getUiSettings().setCompassEnabled(true);
    googleMap.getUiSettings().setRotateGesturesEnabled(true);
    googleMap.getUiSettings().setScrollGesturesEnabled(true);
    googleMap.getUiSettings().setTiltGesturesEnabled(false);
    googleMap.getUiSettings().setZoomControlsEnabled(true);
    googleMap.getUiSettings().setZoomGesturesEnabled(true);
    googleMap.setMinZoomPreference(10.0f);
    googleMap.setMaxZoomPreference(18.0f);
    googleMap.setOnMyLocationButtonClickListener(this);
    resolvePermissions();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (permissionDisposable != null) {
      permissionDisposable.dispose();
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    activity = null;
  }

  private void resolvePermissions() {
    permissionChecker = new PermissionChecker(1002);
    permissionDisposable = permissionChecker.check(this, activity, PERMISSIONS)
        .doFinally(() -> permissionChecker = null)
        .subscribe(this::initLocation, throwable -> new Builder(activity)
            .setTitle(R.string.warning)
            .setMessage(getString(R.string.permissions_required))
            .setPositiveButton(getString(android.R.string.ok),
                (dialog, which) -> activity.runOnUiThread(this::resolvePermissions))
            .setNegativeButton(getString(android.R.string.cancel),
                ((dialog, which) -> initNoLocation()))
            .create()
            .show());
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (permissionChecker != null) {
      permissionChecker.onResult(requestCode, permissions, grantResults);
    }
  }

  @SuppressLint("MissingPermission")
  private void initLocation() {
    if (googleMap == null) {
      return;
    }
    googleMap.setMyLocationEnabled(true);
    // Отправляем в Москву.
    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(55.75583, 37.61778)).zoom(10)
        .build();
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  @SuppressLint("MissingPermission")
  private void initNoLocation() {
    // Отправляем в Москву.
    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(55.75583, 37.61778)).zoom(10)
        .build();
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  @Override
  public boolean onMyLocationButtonClick() {
    //Временно
    @SuppressWarnings("deprecation")
    Location location = googleMap.getMyLocation();
    // Показываем, где он есть.
    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(17)
        .build();
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    return true;
  }
}