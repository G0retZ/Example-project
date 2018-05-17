package com.fasten.executor_driver.application;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewActions;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewModel;
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
  @Nullable
  private AutoRouter autoRouter;
  @Nullable
  private ExecutorStateViewActions executorStateViewActions;

  @Inject
  public void setExecutorStateViewModel(@NonNull ExecutorStateViewModel executorStateViewModel) {
    this.executorStateViewModel = executorStateViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Inject
  public void setAutoRouter(@NonNull AutoRouter autoRouter) {
    this.autoRouter = autoRouter;
  }

  @Inject
  public void setExecutorStateViewActions(
      @NonNull ExecutorStateViewActions executorStateViewActions) {
    this.executorStateViewActions = executorStateViewActions;
  }

  @Inject
  public void setLifeCycleCallbacks(
      @Nullable ActivityLifecycleCallbacks activityLifecycleCallbacks) {
    registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = new AppComponentImpl(this.getApplicationContext());
    appComponent.inject(this);
    if (executorStateViewModel == null || geoLocationViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    executorStateViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null && executorStateViewActions != null) {
        viewState.apply(executorStateViewActions);
      }
    });
    executorStateViewModel.getNavigationLiveData().observeForever(this::navigate);
    geoLocationViewModel.getNavigationLiveData().observeForever(this::navigate);
    initExecutorStates(true);
    initGeoLocation();
  }

  public void initExecutorStates(boolean reload) {
    if (executorStateViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    executorStateViewModel.initializeExecutorState(reload);
  }

  public void initGeoLocation() {
    if (geoLocationViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    geoLocationViewModel.updateGeoLocations();
  }

  private void navigate(@Nullable String destination) {
    if (autoRouter == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (destination == null) {
      return;
    }
    switch (destination) {
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
        startService(R.string.online, R.string.no_orders, R.string.to_app,
            PendingIntent.getActivity(this, 0, new Intent(this, OnlineActivity.class), 0));
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        startService(R.string.online, R.string.wait_for_orders, R.string.to_app,
            PendingIntent.getActivity(this, 0, new Intent(this, OnlineActivity.class), 0));
        break;
      case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
        startService(R.string.offer, R.string.new_order, R.string.consider,
            PendingIntent
                .getActivity(this, 0, new Intent(this, DriverOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        startService(R.string.online, R.string.executing, R.string.to_app,
            PendingIntent.getActivity(this, 0, new Intent(this, OnlineActivity.class), 0));
        break;
    }
    autoRouter.navigateTo(destination);
  }

  @NonNull
  public AppComponent getAppComponent() {
    if (appComponent == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    return appComponent;
  }

  private void startService(@StringRes int title, @StringRes int text, @StringRes int actionText,
      PendingIntent activityPendingIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
          .putExtra(Intent.EXTRA_HTML_TEXT, actionText)
          .putExtra(Intent.EXTRA_INTENT, activityPendingIntent)
      );
    } else {
      startService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
          .putExtra(Intent.EXTRA_HTML_TEXT, actionText)
          .putExtra(Intent.EXTRA_INTENT, activityPendingIntent)
      );
    }
  }

  private void stopService() {
    stopService(new Intent(this, PersistenceService.class));
  }
}
