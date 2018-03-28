package com.fasten.executor_driver.application;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenNavigate;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenViewModel;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application {

  private boolean showError = false;
  @Nullable
  private AppComponent appComponent;
  @Nullable
  private SplashScreenViewModel splashScreenViewModel;
  @Nullable
  private Activity currentActivity;
  private boolean vehicleOptions;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;
  @Nullable
  private Intent routeIntent;
  @NonNull
  private final ActivityLifecycleCallbacks problemsActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
      vehicleOptions = activity instanceof VehicleOptionsActivity;
      currentActivity = activity;
      showNetworkError();
      route();
    }

    @Override
    public void onActivityPaused(Activity activity) {
      if (currentActivity == activity) {
        currentActivity = null;
      }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
  };

  @Inject
  public void setSplashScreenViewModel(@NonNull SplashScreenViewModel splashScreenViewModel) {
    this.splashScreenViewModel = splashScreenViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    registerActivityLifecycleCallbacks(problemsActivityLifecycleCallbacks);
    appComponent = new AppComponentImpl(this.getApplicationContext());
    appComponent.inject(this);
    if (splashScreenViewModel == null || geoLocationViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    splashScreenViewModel.getNavigationLiveData().observeForever(direction -> {
      if (direction != null) {
        navigate(direction);
      }
    });
    geoLocationViewModel.getNavigationLiveData().observeForever(viewState -> {
      if (GeoLocationNavigate.RESOLVE_GEO_PROBLEM.equals(viewState)) {
        routeIntent = new Intent(this, GeolocationResolutionActivity.class);
        routeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        route();
      }
    });
    new Handler().postDelayed(
        this::reInit,
        currentActivity != null && currentActivity instanceof SplashScreenActivity ? 1500 : 0
    );
  }

  public void reInit() {
    if (splashScreenViewModel == null || geoLocationViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    splashScreenViewModel.initializeApp();
    geoLocationViewModel.updateGeoLocations();
  }

  private void navigate(@NonNull String direction) {
    switch (direction) {
      case SplashScreenNavigate.NO_NETWORK:
        stopService();
        showError = true;
        showNetworkError();
        break;
      case SplashScreenNavigate.AUTHORIZE:
        stopService();
        routeIntent = new Intent(this, LoginActivity.class);
        routeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        route();
        break;
      case SplashScreenNavigate.MAP_SHIFT_CLOSED:
        stopService();
        routeIntent = new Intent(this, MapActivity.class);
        routeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        route();
        break;
      case SplashScreenNavigate.MAP_SHIFT_OPENED:
        startService(R.string.online, R.string.no_orders);
        if (!vehicleOptions) {
          routeIntent = new Intent(this, MapActivity.class);
          routeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          route();
        }
        break;
      case SplashScreenNavigate.MAP_ONLINE:
        startService(R.string.online, R.string.wait_for_orders);
        routeIntent = new Intent(this, OnlineActivity.class);
        routeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        route();
        break;
    }
  }

  @NonNull
  public AppComponent getAppComponent() {
    if (appComponent == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    return appComponent;
  }

  public void startService(@StringRes int title, @StringRes int text) {
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

  public void stopService() {
    stopService(new Intent(this, PersistenceService.class));
  }

  private void route() {
    if (routeIntent != null && currentActivity != null) {
      startActivity(routeIntent);
      routeIntent = null;
    }
  }

  private void showNetworkError() {
    if (currentActivity != null && showError) {
      new Builder(currentActivity)
          .setTitle(R.string.error)
          .setMessage("Без сети не работаем!")
          .setCancelable(false)
          .setPositiveButton(
              getString(android.R.string.ok),
              (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
          )
          .create()
          .show();
    }
  }
}
