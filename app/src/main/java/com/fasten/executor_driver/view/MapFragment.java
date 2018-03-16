package com.fasten.executor_driver.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.application.BaseActivity;
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
    OnMyLocationButtonClickListener, MapViewActions {

  @Nullable
  private BaseActivity baseActivity;
  private MapViewModel mapViewModel;

  private GeoJsonLayer layer;
  private GoogleMap googleMap;
  private Activity activity;

  @Inject
  public void setMapViewModel(@NonNull MapViewModel mapViewModel) {
    this.mapViewModel = mapViewModel;
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
    initLocation();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    activity = null;
  }

  private void initLocation() {
    if (googleMap == null) {
      return;
    }
//    googleMap.setMyLocationEnabled(true);
    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(55.737199, 37.628161)).zoom(15.2f)
        .build();
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  @Override
  public boolean onMyLocationButtonClick() {
    // Показываем, где он есть.
//    CameraPosition cameraPosition = new CameraPosition.Builder()
//        .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15.2f)
//        .build();
//    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
}