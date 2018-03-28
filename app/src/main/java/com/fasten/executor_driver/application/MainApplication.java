package com.fasten.executor_driver.application;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application {

  @Nullable
  private AppComponent appComponent;
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;

  @Inject
  public void setExecutorStateViewModel(@NonNull ExecutorStateViewModel executorStateViewModel) {
    this.executorStateViewModel = executorStateViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = new AppComponentImpl(this.getApplicationContext());
    appComponent.inject(this);
    if (executorStateViewModel == null || geoLocationViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    executorStateViewModel.getNavigationLiveData().observeForever(this::navigate);
    geoLocationViewModel.getNavigationLiveData().observeForever(this::navigate);
    executorStateViewModel.initializeExecutorState();
    geoLocationViewModel.updateGeoLocations();
  }

  private void navigate(@Nullable String direction) {
    if (direction != null) {
      switch (direction) {
        case GeoLocationNavigate.RESOLVE_GEO_PROBLEM:
          stopService();
          break;
        case ExecutorStateNavigate.NO_NETWORK:
          stopService();
          break;
        case ExecutorStateNavigate.AUTHORIZE:
          stopService();
          break;
        case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
          stopService();
          break;
        case ExecutorStateNavigate.MAP_SHIFT_OPENED:
          startService(R.string.online, R.string.no_orders);
          break;
        case ExecutorStateNavigate.MAP_ONLINE:
          startService(R.string.online, R.string.wait_for_orders);
          break;
      }
    }
  }

  @NonNull
  public AppComponent getAppComponent() {
    if (appComponent == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    return appComponent;
  }

  @SuppressWarnings("SameParameterValue")
  private void startService(@StringRes int title, @StringRes int text) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
      );
    } else {
      startService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
      );
    }
  }

  private void stopService() {
    stopService(new Intent(this, PersistenceService.class));
  }
}
