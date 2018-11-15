package com.cargopull.executor_driver.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewActions;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.cargopull.executor_driver.presentation.map.MapViewActions;
import com.cargopull.executor_driver.presentation.map.MapViewModel;
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

  @Inject
  public void setMapViewModel(@NonNull MapViewModel mapViewModel) {
    this.mapViewModel = mapViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
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
    getMapAsync(this);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (baseActivity != null) {
      baseActivity.getDiComponent().inject(this);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (googleMap != null) {
      geoLocationViewModel.updateGeoLocations();
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    if (baseActivity == null) {
      throw new IllegalStateException("Shit! WTF?!");
    }
    this.googleMap = googleMap;
    try {
      googleMap.setMapStyle(
          MapStyleOptions.loadRawResourceStyle(baseActivity, R.raw.map_style)
      );
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
    googleMap.setPadding(0, 0, 0, 128);
    googleMap.setMinZoomPreference(8.0f);
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
    geoLocationViewModel.updateGeoLocations();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    baseActivity = null;
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