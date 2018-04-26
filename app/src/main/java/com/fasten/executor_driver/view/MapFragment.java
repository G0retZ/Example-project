package com.fasten.executor_driver.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewActions;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.fasten.executor_driver.presentation.map.MapViewActions;
import com.fasten.executor_driver.presentation.map.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends BaseFragment implements OnMapReadyCallback,
    OnMyLocationButtonClickListener, MapViewActions, GeoLocationViewActions {

  private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

  @Nullable
  private GeoLocation lastLocation;
  private MapViewModel mapViewModel;
  private GeoLocationViewModel geoLocationViewModel;

  private GeoJsonLayer layer;
  private GoogleMap googleMap;
  private Context context;
  private MapView mapView;

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
    this.context = context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mapView = view.findViewById(R.id.map);
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    super.onDependencyInject(appComponent);
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // *** IMPORTANT ***
    // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
    // objects or sub-Bundles.
    Bundle mapViewBundle = null;
    if (savedInstanceState != null) {
      mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
    }
    mapView.onCreate(mapViewBundle);
    mapView.getMapAsync(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
    if (googleMap != null) {
      geoLocationViewModel.updateGeoLocations();
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
    try {
      googleMap.setMapStyle(
          MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_aubergine)
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
    geoLocationViewModel.updateGeoLocations();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
    if (mapViewBundle == null) {
      mapViewBundle = new Bundle();
      outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
    }
    mapView.onSaveInstanceState(mapViewBundle);
  }

  @Override
  public void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
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