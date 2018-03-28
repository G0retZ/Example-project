package com.fasten.executor_driver.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.application.BaseActivity;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewActions;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.fasten.executor_driver.presentation.map.MapViewActions;
import com.fasten.executor_driver.presentation.map.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
    OnMyLocationButtonClickListener, MapViewActions, GeoLocationViewActions {

  @Nullable
  private BaseActivity baseActivity;
  @Nullable
  private GeoLocation lastLocation;
  private MapViewModel mapViewModel;
  private GeoLocationViewModel geoLocationViewModel;

  private GeoJsonLayer layer;
  private GoogleMap googleMap;
  private Activity activity;

  @Inject
  public void setMapViewModel(@NonNull MapViewModel mapViewModel) {
    this.mapViewModel = mapViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      baseActivity = (BaseActivity) context;
    } catch (ClassCastException e) {
      Log.w(this.getClass().getName(), context.getClass().getName() +
          " не наследует BaseActivity. OnBackPressed никогда не будет вызван.");
    }
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if (baseActivity != null) {
      baseActivity.getDiComponent().inject(this);
    }
    getMapAsync(this);
  }

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
    mapViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    geoLocationViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    activity = null;
  }

  @Override
  public boolean onMyLocationButtonClick() {
    if (googleMap == null || lastLocation == null) {
      return false;
    }
    // Показываем, где он есть.
    googleMap.animateCamera(
        CameraUpdateFactory.newCameraPosition(
            new CameraPosition.Builder()
                .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .zoom(15.2f)
                .build()
        )
    );
    return true;
  }

  @Override
  public void updateHeatMap(@Nullable String geoJson) {
    if (geoJson == null) {
      return;
    }
    try {
      JSONObject jsonObject = new JSONObject(geoJson);
      if (layer != null) {
        layer.removeLayerFromMap();
      }
      layer = new GeoJsonLayer(googleMap, jsonObject);
      GeoJsonPolygonStyle polygonStyle = new GeoJsonPolygonStyle();
      polygonStyle.setFillColor(0x2000FF00);
      polygonStyle.setStrokeColor(0x80FF8080);
      for (GeoJsonFeature feature : layer.getFeatures()) {
        feature.setPolygonStyle(polygonStyle);
      }
      layer.addLayerToMap();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @SuppressLint("MissingPermission")
  @Override
  public void updateLocation(@NonNull GeoLocation geoLocation) {
    lastLocation = geoLocation;
    if (googleMap == null) {
      return;
    }
    if (!googleMap.isMyLocationEnabled()) {
      googleMap.setMyLocationEnabled(true);
      googleMap.animateCamera(
          CameraUpdateFactory.newCameraPosition(
              new CameraPosition.Builder()
                  .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                  .zoom(15.2f)
                  .build()
          )
      );
    }
  }
}