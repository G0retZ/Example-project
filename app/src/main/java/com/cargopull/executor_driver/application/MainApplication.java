package com.cargopull.executor_driver.application;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat.BigTextStyle;
import androidx.core.app.NotificationCompat.Builder;
import com.cargopull.executor_driver.AppConfigKt;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.di.BackendComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewActions;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModel;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewActions;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModel;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModel;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewActions;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModel;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListNavigate;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionNavigate;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewActions;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewActions;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewModel;
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewActions;
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewModel;
import com.cargopull.executor_driver.utils.Consumer;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application implements ServerConnectionViewActions,
    MissedOrderViewActions, PreOrderViewActions, UpcomingPreOrderMessageViewActions,
    CancelledOrderViewActions, UpcomingPreOrderViewActions {

  @Nullable
  private Activity currentActivity;
  private AppComponent appComponent;
  private RingTonePlayer ringTonePlayer;
  private ShakeItPlayer shakeItPlayer;
  private ServerConnectionViewModel serverConnectionViewModel;
  private BalanceViewModel balanceViewModel;
  private ExecutorStateViewModel executorStateViewModel;
  private OrderViewModel orderViewModel;
  private OrderRouteViewModel orderRouteViewModel;
  private PreOrderViewModel preOrderViewModel;
  private UpcomingPreOrderViewModel upcomingPreOrderViewModel;
  private PreOrdersListViewModel preOrdersListViewModel;
  private OrderCostDetailsViewModel orderCostDetailsViewModel;
  private GeoLocationViewModel geoLocationViewModel;
  private MissedOrderViewModel missedOrderViewModel;
  private UpcomingPreOrderMessageViewModel upcomingPreOrderMessageViewModel;
  private CancelledOrderViewModel cancelledOrderViewModel;
  private CurrentCostPollingViewModel currentCostPollingViewModel;
  private ServerTimeViewModel serverTimeViewModel;
  private NavigationMapper navigationMapper;
  private int missedOrdersCount;
  private NotificationManager notificationManager;

  @Inject
  public void setRingTonePlayer(@NonNull RingTonePlayer ringTonePlayer) {
    this.ringTonePlayer = ringTonePlayer;
  }

  @Inject
  public void setShakeItPlayer(@NonNull ShakeItPlayer shakeItPlayer) {
    this.shakeItPlayer = shakeItPlayer;
  }

  @Inject
  public void setServerConnectionViewModel(
      @NonNull ServerConnectionViewModel serverConnectionViewModel) {
    this.serverConnectionViewModel = serverConnectionViewModel;
  }

  @Inject
  public void setExecutorStateViewModel(@NonNull ExecutorStateViewModel executorStateViewModel) {
    this.executorStateViewModel = executorStateViewModel;
  }

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
  }

  @Inject
  public void setOrderRouteViewModel(@NonNull OrderRouteViewModel orderRouteViewModel) {
    this.orderRouteViewModel = orderRouteViewModel;
  }

  @Inject
  public void setPreOrderViewModel(@NonNull PreOrderViewModel preOrderViewModel) {
    this.preOrderViewModel = preOrderViewModel;
  }

  @Inject
  public void setUpcomingPreOrderViewModel(
      @NonNull UpcomingPreOrderViewModel upcomingPreOrderViewModel) {
    this.upcomingPreOrderViewModel = upcomingPreOrderViewModel;
  }

  @Inject
  public void setPreOrdersListViewModel(@NonNull PreOrdersListViewModel preOrdersListViewModel) {
    this.preOrdersListViewModel = preOrdersListViewModel;
  }

  @Inject
  public void setOrderCostDetailsViewModel(
      @NonNull OrderCostDetailsViewModel orderCostDetailsViewModel) {
    this.orderCostDetailsViewModel = orderCostDetailsViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Inject
  public void setNavigationMapper(@NonNull NavigationMapper navigationMapper) {
    this.navigationMapper = navigationMapper;
  }

  @Inject
  public void setBalanceViewModel(@NonNull BalanceViewModel balanceViewModel) {
    this.balanceViewModel = balanceViewModel;
  }

  @Inject
  public void setMissedOrderViewModel(@NonNull MissedOrderViewModel missedOrderViewModel) {
    this.missedOrderViewModel = missedOrderViewModel;
  }

  @Inject
  public void setUpcomingPreOrderMessageViewModel(
      @NonNull UpcomingPreOrderMessageViewModel upcomingPreOrderMessageViewModel) {
    this.upcomingPreOrderMessageViewModel = upcomingPreOrderMessageViewModel;
  }

  @Inject
  public void setCancelledOrderViewModel(@NonNull CancelledOrderViewModel cancelledOrderViewModel) {
    this.cancelledOrderViewModel = cancelledOrderViewModel;
  }

  @Inject
  public void setCurrentCostPollingViewModel(
      @NonNull CurrentCostPollingViewModel currentCostPollingViewModel) {
    this.currentCostPollingViewModel = currentCostPollingViewModel;
  }

  @Inject
  public void setServerTimeViewModel(@NonNull ServerTimeViewModel serverTimeViewModel) {
    this.serverTimeViewModel = serverTimeViewModel;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = new AppComponent(this, new BackendComponent(this));
    appComponent.inject(
        appSettingsService -> AppCompatDelegate.setDefaultNightMode(
            appSettingsService.getNumber("mode")
        )
    );
    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
      }

      @Override
      public void onActivityStarted(Activity activity) {
        currentActivity = activity;
        if (navigationMapper != null) {
          navigationMapper.navigateToRecent().accept(activity);
        }
      }

      @Override
      public void onActivityResumed(Activity activity) {
      }

      @Override
      public void onActivityPaused(Activity activity) {
      }

      @Override
      public void onActivityStopped(Activity activity) {
        if (currentActivity == activity) {
          currentActivity = null;
        }
      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
      }
    });
    // Постим инициализацию, чтобы не задерживать само приложение
    new Handler().post(this::initApplication);
  }

  private void initApplication() {
    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    initNotificationChannels(notificationManager);
    appComponent.inject(this);
    serverConnectionViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    missedOrderViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    upcomingPreOrderMessageViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    cancelledOrderViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    preOrderViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    upcomingPreOrderViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    serverConnectionViewModel.getNavigationLiveData().observeForever(this::navigate);
    balanceViewModel.getNavigationLiveData().observeForever(this::navigate);
    executorStateViewModel.getNavigationLiveData().observeForever(this::navigate);
    orderViewModel.getNavigationLiveData().observeForever(this::navigate);
    orderCostDetailsViewModel.getNavigationLiveData().observeForever(this::navigate);
    preOrdersListViewModel.getNavigationLiveData().observeForever(this::navigate);
    geoLocationViewModel.getNavigationLiveData().observeForever(this::navigate);
    currentCostPollingViewModel.getNavigationLiveData().observeForever(this::navigate);
    serverTimeViewModel.getNavigationLiveData().observeForever(this::navigate);
    initServerConnection();
  }

  private void initNotificationChannels(@NonNull NotificationManager notificationManager) {
    // Android O требует Notification Channel.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Бесшумный важный канал для сообщений со своим звуком и вибрацией
      NotificationChannel mChannel =
          new NotificationChannel(AppConfigKt.QUIET_CHANNEL_ID,
              getString(R.string.server_connection),
              NotificationManager.IMPORTANCE_HIGH);
      mChannel.setDescription(getString(R.string.server_connection_desc));
      mChannel.setSound(null, null);
      mChannel.enableVibration(false);
      notificationManager.createNotificationChannel(mChannel);

      // Обычный канал для информационных сообщений общего характера
      mChannel =
          new NotificationChannel(AppConfigKt.ANNOUNCEMENT_CHANNEL_ID,
              getString(R.string.important_info),
              NotificationManager.IMPORTANCE_DEFAULT);
      mChannel.setDescription(getString(R.string.important_info_desc));
      notificationManager.createNotificationChannel(mChannel);
    }
  }

  public void initServerConnection() {
    serverConnectionViewModel.connectServer();
  }

  public void initGeoLocation() {
    geoLocationViewModel.updateGeoLocations();
  }

  @Override
  public void showConnectionReady(boolean connected) {
    if (connected) {
      initGeoLocation();
    } else {
      navigate(CommonNavigate.NO_CONNECTION);
    }
  }

  void navigate(@Nullable String destination) {
    if (destination == null) {
      return;
    }
    switch (destination) {
      case ServerConnectionNavigate.AUTHORIZE:
        stopService();
        break;
      case ServerConnectionNavigate.VERSION_DEPRECATED:
        stopService();
        break;
      case CommonNavigate.SERVER_DATA_ERROR:
        stopService();
        break;
      case CommonNavigate.EXIT:
        serverConnectionViewModel.disconnectServer();
        stopService();
        break;
      case ExecutorStateNavigate.BLOCKED:
        stopService();
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        stopService();
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        stopService();
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        startService(R.string.online, R.string.wait_for_orders, PendingIntent
            .getActivity(this, 0, new Intent(this, OnlineActivity.class), 0));
        break;
      case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
        ringTonePlayer.playRingTone(R.raw.regular_order_notify);
        shakeItPlayer.shakeIt(R.raw.regular_order_notify_vibro);
        startService(R.string.offer, R.string.new_order, PendingIntent
            .getActivity(this, 0, new Intent(this, DriverOrderConfirmationActivity.class), 0));
        navigationMapper.navigateTo(destination).accept(this);
        return;
      case ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION:
        ringTonePlayer.playRingTone(R.raw.regular_order_notify);
        shakeItPlayer.shakeIt(R.raw.regular_order_notify_vibro);
        startService(R.string.preliminary_order, R.string.time_to_set_out, PendingIntent
            .getActivity(this, 0, new Intent(this, DriverPreOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        ringTonePlayer.playRingTone(R.raw.accept_offer);
        shakeItPlayer.shakeIt(R.raw.accept_offer_vibro);
        startService(R.string.working, R.string.client_confirm, PendingIntent
            .getActivity(this, 0, new Intent(this, ClientOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.MOVING_TO_CLIENT:
        startService(R.string.working, R.string.moving_to_client, PendingIntent
            .getActivity(this, 0, new Intent(this, MovingToClientActivity.class), 0));
        navigationMapper.navigateTo(destination).accept(this);
        return;
      case ExecutorStateNavigate.WAITING_FOR_CLIENT:
        startService(R.string.working, R.string.wait_for_client, PendingIntent
            .getActivity(this, 0, new Intent(this, WaitingForClientActivity.class), 0));
        break;
      case ExecutorStateNavigate.ORDER_FULFILLMENT:
        startService(R.string.working, R.string.order_fulfillment, PendingIntent
            .getActivity(this, 0, new Intent(this, OrderFulfillmentActivity.class), 0));
        break;
      case ExecutorStateNavigate.PAYMENT_CONFIRMATION:
        startService(R.string.working, R.string.order_fulfillment, PendingIntent
            .getActivity(this, 0, new Intent(this, OrderCostDetailsActivity.class), 0));
        break;
      case PreOrderNavigate.ORDER_APPROVAL:
        ringTonePlayer.playRingTone(R.raw.preliminary_order_notify);
        shakeItPlayer.shakeIt(R.raw.preliminary_order_notify_vibro);
        break;
      case PreOrdersListNavigate.PRE_ORDER:
        return;
    }
    Consumer<Context> consumer = navigationMapper.navigateTo(destination);
    if (currentActivity != null) {
      consumer.accept(currentActivity);
    }
  }

  @NonNull
  public AppComponent getAppComponent() {
    if (appComponent == null) {
      throw new RuntimeException("Граф зависимостей поломан!");
    }
    return appComponent;
  }

  @Override
  public void showMissedOrderMessage(@NonNull String message) {
    ringTonePlayer.playRingTone(R.raw.missed_offer);
    shakeItPlayer.shakeIt(R.raw.missed_order_vibro);
    Builder builder = new Builder(this, AppConfigKt.QUIET_CHANNEL_ID)
        .setContentTitle(getString(R.string.missed_order))
        .setContentText(message)
        .setStyle(new BigTextStyle().bigText(message))
        .setSound(null)
        .setVibrate(new long[0])
        .setAutoCancel(true)
        .setContentIntent(
            PendingIntent.getActivity(this, 0, new Intent(this, BalanceActivity.class), 0)
        )
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(getString(R.string.missed_order))
        .setWhen(System.currentTimeMillis());
    notificationManager.notify(missedOrdersCount++ % 5, builder.build());
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
  public void showPreOrderAvailable(boolean show) {
    if (show) {
      navigate(PreOrderNavigate.ORDER_APPROVAL);
      Builder builder = new Builder(this, "state_channel")
          .setContentTitle(getString(R.string.new_pre_order))
          .setContentText(getString(R.string.new_pre_order_message))
          .setStyle(new BigTextStyle().bigText(getString(R.string.new_pre_order_message)))
          .setSound(null)
          .setVibrate(new long[0])
          .setContentIntent(
              PendingIntent.getActivity(this, 0,
                  new Intent(this, DriverPreOrderBookingActivity.class), 0)
          )
          .setSmallIcon(R.mipmap.ic_launcher)
          .setTicker(getString(R.string.new_pre_order))
          .setWhen(System.currentTimeMillis());
      notificationManager.notify(7, builder.build());
    } else {
      notificationManager.cancel(7);
    }
  }

  @Override
  public void showUpcomingPreOrderMessage(@NonNull String message) {
    shakeItPlayer.shakeIt(R.raw.pre_order_reminder_vibro);
    ringTonePlayer.playRingTone(R.raw.pre_order_reminder);
    Builder builder = new Builder(this, AppConfigKt.QUIET_CHANNEL_ID)
        .setContentText(message)
        .setStyle(new BigTextStyle().bigText(message))
        .setContentTitle(getString(R.string.upcoming_pre_order))
        .setSound(null)
        .setVibrate(new long[0])
        .setAutoCancel(true)
        .setContentIntent(
            PendingIntent.getActivity(this, 0,
                new Intent(this, UpcomingPreOrderActivity.class), 0)
        )
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(getString(R.string.upcoming_pre_order))
        .setWhen(System.currentTimeMillis());
    notificationManager.notify(8, builder.build());
  }

  @Override
  public void showUpcomingPreOrderAvailable(boolean show) {
    if (!show) {
      notificationManager.cancel(8);
    }
  }

  @Override
  public void showCancelledOrderMessage(@NonNull String message) {
    ringTonePlayer.playRingTone(R.raw.missed_offer);
    Builder builder = new Builder(this, "state_channel")
        .setContentTitle(getString(R.string.order_cancelled))
        .setContentText(message)
        .setStyle(new BigTextStyle().bigText(message))
        .setSound(null)
        .setVibrate(new long[0])
        .setAutoCancel(true)
        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(getString(R.string.order_cancelled))
        .setWhen(System.currentTimeMillis());
    notificationManager.notify(11, builder.build());
  }
}
