package com.fasten.executor_driver.application;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat.Builder;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsNavigate;
import com.fasten.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModel;
import com.fasten.executor_driver.presentation.coreBalance.CoreBalanceNavigate;
import com.fasten.executor_driver.presentation.coreBalance.CoreBalanceViewModel;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewActions;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.fasten.executor_driver.presentation.missedorder.MissedOrderViewActions;
import com.fasten.executor_driver.presentation.missedorder.MissedOrderViewModel;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application implements MissedOrderViewActions {

  @Nullable
  private AppComponent appComponent;
  @Nullable
  private CancelOrderReasonsViewModel cancelOrderReasonsViewModel;
  @Nullable
  private CoreBalanceViewModel coreBalanceViewModel;
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;
  @Nullable
  private MissedOrderViewModel missedOrderViewModel;
  @Nullable
  private AutoRouter autoRouter;
  @Nullable
  private ExecutorStateViewActions executorStateViewActions;
  private int missedOrdersCount;
  @Nullable
  private NotificationManager notificationManager;

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
  public void setCancelOrderReasonsViewModel(
      @NonNull CancelOrderReasonsViewModel cancelOrderReasonsViewModel) {
    this.cancelOrderReasonsViewModel = cancelOrderReasonsViewModel;
  }

  @Inject
  public void setCoreBalanceViewModel(@NonNull CoreBalanceViewModel coreBalanceViewModel) {
    this.coreBalanceViewModel = coreBalanceViewModel;
  }

  @Inject
  public void setMissedOrderViewModel(@Nullable MissedOrderViewModel missedOrderViewModel) {
    this.missedOrderViewModel = missedOrderViewModel;
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
    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    appComponent = new AppComponentImpl(this.getApplicationContext());
    appComponent.inject(this);
    if (cancelOrderReasonsViewModel == null || coreBalanceViewModel == null ||
        executorStateViewModel == null || geoLocationViewModel == null
        || missedOrderViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    executorStateViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null && executorStateViewActions != null) {
        viewState.apply(executorStateViewActions);
      }
    });
    missedOrderViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null && executorStateViewActions != null) {
        viewState.apply(this);
      }
    });
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(this::navigate);
    coreBalanceViewModel.getNavigationLiveData().observeForever(this::navigate);
    executorStateViewModel.getNavigationLiveData().observeForever(this::navigate);
    geoLocationViewModel.getNavigationLiveData().observeForever(this::navigate);
    initExecutorStates(true);
    initGeoLocation();
  }

  public void initExecutorStates(boolean reload) {
    if (cancelOrderReasonsViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (coreBalanceViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (executorStateViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (missedOrderViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    cancelOrderReasonsViewModel.initializeCancelOrderReasons(reload);
    coreBalanceViewModel.initializeExecutorBalance(reload);
    executorStateViewModel.initializeExecutorState(reload);
    missedOrderViewModel.initializeMissedOrderMessages();
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
      case CancelOrderReasonsNavigate.SERVER_DATA_ERROR:
        stopService();
        break;
      case CoreBalanceNavigate.SERVER_DATA_ERROR:
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
        startService(R.string.online, R.string.no_orders, PendingIntent
            .getActivity(this, 0, new Intent(this, OnlineActivity.class), 0));
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        startService(R.string.online, R.string.wait_for_orders, PendingIntent
            .getActivity(this, 0, new Intent(this, OnlineActivity.class), 0));
        break;
      case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
        startService(R.string.offer, R.string.new_order, PendingIntent
            .getActivity(this, 0, new Intent(this, DriverOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        startService(R.string.working, R.string.client_confirm, PendingIntent
            .getActivity(this, 0, new Intent(this, ClientOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.MOVING_TO_CLIENT:
        startService(R.string.working, R.string.moving_to_client, PendingIntent
            .getActivity(this, 0, new Intent(this, MovingToClientActivity.class), 0));
        break;
      case ExecutorStateNavigate.WAITING_FOR_CLIENT:
        startService(R.string.working, R.string.wait_for_client, PendingIntent
            .getActivity(this, 0, new Intent(this, WaitingForClientActivity.class), 0));
        break;
      case ExecutorStateNavigate.ORDER_FULFILLMENT:
        startService(R.string.working, R.string.order_fulfillment, PendingIntent
            .getActivity(this, 0, new Intent(this, OrderFulfillmentActivity.class), 0));
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

  private void startService(@StringRes int title, @StringRes int text,
      PendingIntent activityPendingIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
          .putExtra(Intent.EXTRA_INTENT, activityPendingIntent)
      );
    } else {
      startService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
          .putExtra(Intent.EXTRA_INTENT, activityPendingIntent)
      );
    }
  }

  private void stopService() {
    stopService(new Intent(this, PersistenceService.class));
  }

  @Override
  public void showMissedOrderMessage(@NonNull String message) {
    if (notificationManager != null) {
      Builder builder = new Builder(this, "state_channel")
          .setContentText(getString(R.string.missed_order))
          .setContentTitle(message)
          .setAutoCancel(true)
          .setContentIntent(
              PendingIntent.getActivity(this, 0, new Intent(this, BalanceActivity.class), 0)
          )
          .setSmallIcon(R.mipmap.ic_launcher)
          .setTicker(getString(R.string.missed_order))
          .setWhen(System.currentTimeMillis());
      notificationManager.notify(missedOrdersCount++ % 5, builder.build());
    }
  }
}
