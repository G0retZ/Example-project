package com.cargopull.executor_driver.application;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.di.AppComponentImpl;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewActions;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModel;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModel;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewActions;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModel;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
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
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application implements ServerConnectionViewActions,
    MissedOrderViewActions, PreOrderViewActions, UpcomingPreOrderViewActions,
    CancelledOrderViewActions {

  @Nullable
  private AppComponent appComponent;
  @Nullable
  private RingTonePlayer ringTonePlayer;
  @Nullable
  private ShakeItPlayer shakeItPlayer;
  @Nullable
  private ServerConnectionViewModel serverConnectionViewModel;
  @Nullable
  private CancelOrderReasonsViewModel cancelOrderReasonsViewModel;
  @Nullable
  private BalanceViewModel balanceViewModel;
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private OrderViewModel orderViewModel;
  @Nullable
  private PreOrderViewModel preOrderViewModel;
  @Nullable
  private PreOrdersListViewModel preOrdersListViewModel;
  @Nullable
  private OrderCostDetailsViewModel orderCostDetailsViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;
  @Nullable
  private MissedOrderViewModel missedOrderViewModel;
  @Nullable
  private UpcomingPreOrderViewModel upcomingPreOrderViewModel;
  @Nullable
  private CancelledOrderViewModel cancelledOrderViewModel;
  @Nullable
  private UpdateMessageViewModel updateMessageViewModel;
  @Nullable
  private CurrentCostPollingViewModel currentCostPollingViewModel;
  @Nullable
  private ServerTimeViewModel serverTimeViewModel;
  @Nullable
  private AutoRouter autoRouter;
  private int missedOrdersCount;
  @Nullable
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
  public void setPreOrderViewModel(@NonNull PreOrderViewModel preOrderViewModel) {
    this.preOrderViewModel = preOrderViewModel;
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
  public void setAutoRouter(@NonNull AutoRouter autoRouter) {
    this.autoRouter = autoRouter;
  }

  @Inject
  public void setCancelOrderReasonsViewModel(
      @NonNull CancelOrderReasonsViewModel cancelOrderReasonsViewModel) {
    this.cancelOrderReasonsViewModel = cancelOrderReasonsViewModel;
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
  public void setUpcomingPreOrderViewModel(
      @Nullable UpcomingPreOrderViewModel upcomingPreOrderViewModel) {
    this.upcomingPreOrderViewModel = upcomingPreOrderViewModel;
  }

  @Inject
  public void setCancelledOrderViewModel(@NonNull CancelledOrderViewModel cancelledOrderViewModel) {
    this.cancelledOrderViewModel = cancelledOrderViewModel;
  }

  @Inject
  public void setUpdateMessageViewModel(@NonNull UpdateMessageViewModel updateMessageViewModel) {
    this.updateMessageViewModel = updateMessageViewModel;
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

  @Inject
  public void setLifeCycleCallbacks(
      @NonNull ActivityLifecycleCallbacks activityLifecycleCallbacks) {
    registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    appComponent = new AppComponentImpl(this.getApplicationContext());
    appComponent.inject(this);
    if (cancelOrderReasonsViewModel == null || balanceViewModel == null ||
        executorStateViewModel == null || geoLocationViewModel == null
        || missedOrderViewModel == null || updateMessageViewModel == null
        || cancelledOrderViewModel == null || serverConnectionViewModel == null
        || currentCostPollingViewModel == null || serverTimeViewModel == null
        || orderViewModel == null || preOrderViewModel == null || orderCostDetailsViewModel == null
        || upcomingPreOrderViewModel == null || preOrdersListViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
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
    upcomingPreOrderViewModel.getViewStateLiveData().observeForever(viewState -> {
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
    serverConnectionViewModel.getNavigationLiveData().observeForever(this::navigate);
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(this::navigate);
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

  public void initServerConnection() {
    if (serverConnectionViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    serverConnectionViewModel.connectServer();
  }

  public void initGeoLocation() {
    if (geoLocationViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
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
    if (autoRouter == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (serverConnectionViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
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
        playSound(R.raw.new_offer);
        shakeIt(R.raw.new_offer_vibro);
        startService(R.string.offer, R.string.new_order, PendingIntent
            .getActivity(this, 0, new Intent(this, DriverOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION:
        playSound(R.raw.new_offer);
        shakeIt(R.raw.new_offer_vibro);
        startService(R.string.preliminary_order, R.string.time_to_set_out, PendingIntent
            .getActivity(this, 0, new Intent(this, DriverPreOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        playSound(R.raw.accept_offer);
        shakeIt(R.raw.accept_offer_vibro);
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
      case ExecutorStateNavigate.PAYMENT_CONFIRMATION:
        startService(R.string.working, R.string.order_fulfillment, PendingIntent
            .getActivity(this, 0, new Intent(this, OrderCostDetailsActivity.class), 0));
        break;
      case PreOrderNavigate.ORDER_APPROVAL:
        playSound(R.raw.new_pre_order);
        shakeIt(R.raw.new_pre_order_vibro);
        break;
      case PreOrdersListNavigate.PRE_ORDER:
        return;
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

  @Override
  public void showMissedOrderMessage(@NonNull String message) {
    playSound(R.raw.missed_offer);
    shakeIt(R.raw.missed_order_vibro);
    if (notificationManager != null) {
      Builder builder = new Builder(this, "state_channel")
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

  private void playSound(@RawRes int rawId) {
    if (ringTonePlayer == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    ringTonePlayer.playRingTone(rawId);
  }

  private void shakeIt(@RawRes int patternId) {
    if (shakeItPlayer == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    shakeItPlayer.shakeIt(patternId);
  }

  @Override
  public void showPreOrderAvailable(boolean show) {
    if (notificationManager != null) {
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
  }

  @Override
  public void showUpcomingPreOrderMessage(@NonNull String message) {
    shakeIt(R.raw.accept_offer_vibro);
    if (notificationManager != null) {
      Builder builder = new Builder(this, "state_channel")
          .setContentText(message)
          .setStyle(new BigTextStyle().bigText(message))
          .setContentTitle(getString(R.string.upcoming_pre_order))
          .setSound(null)
          .setVibrate(new long[0])
          .setAutoCancel(true)
          .setSmallIcon(R.mipmap.ic_launcher)
          .setTicker(getString(R.string.upcoming_pre_order))
          .setWhen(System.currentTimeMillis());
      notificationManager.notify(8, builder.build());
    }
  }

  @Override
  public void showCancelledOrderMessage(@NonNull String message) {
    playSound(R.raw.missed_offer);
    if (notificationManager != null) {
      Builder builder = new Builder(this, "state_channel")
          .setContentTitle(getString(R.string.order_cancelled))
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
      notificationManager.notify(11, builder.build());
    }
  }
}
