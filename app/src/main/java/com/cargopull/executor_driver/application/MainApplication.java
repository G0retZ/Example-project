package com.cargopull.executor_driver.application;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat.Builder;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.di.AppComponentImpl;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModel;
import com.cargopull.executor_driver.presentation.corebalance.CoreBalanceViewModel;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewActions;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionNavigate;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewActions;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel;
import com.cargopull.executor_driver.utils.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends MultiDexApplication implements ServerConnectionViewActions,
    MissedOrderViewActions {

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
  private CoreBalanceViewModel coreBalanceViewModel;
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;
  @Nullable
  private MissedOrderViewModel missedOrderViewModel;
  @Nullable
  private UpdateMessageViewModel updateMessageViewModel;
  @Nullable
  private CurrentCostPollingViewModel currentCostPollingViewModel;
  @Nullable
  private AutoRouter autoRouter;
  private int missedOrdersCount;
  @Nullable
  private NotificationManager notificationManager;

  @Inject
  public void setRingTonePlayer(@Nullable RingTonePlayer ringTonePlayer) {
    this.ringTonePlayer = ringTonePlayer;
  }

  @Inject
  public void setShakeItPlayer(@Nullable ShakeItPlayer shakeItPlayer) {
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
  public void setMissedOrderViewModel(@NonNull MissedOrderViewModel missedOrderViewModel) {
    this.missedOrderViewModel = missedOrderViewModel;
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
        || missedOrderViewModel == null || updateMessageViewModel == null
        || serverConnectionViewModel == null || currentCostPollingViewModel == null) {
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
    serverConnectionViewModel.getNavigationLiveData().observeForever(this::navigate);
    cancelOrderReasonsViewModel.getNavigationLiveData().observeForever(this::navigate);
    coreBalanceViewModel.getNavigationLiveData().observeForever(this::navigate);
    executorStateViewModel.getNavigationLiveData().observeForever(this::navigate);
    geoLocationViewModel.getNavigationLiveData().observeForever(this::navigate);
    currentCostPollingViewModel.getNavigationLiveData().observeForever(this::navigate);
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
      if (updateMessageViewModel == null) {
        throw new IllegalStateException("Граф зависимостей поломан!");
      }
      if (currentCostPollingViewModel == null) {
        throw new IllegalStateException("Граф зависимостей поломан!");
      }
      executorStateViewModel.initializeExecutorState();
      cancelOrderReasonsViewModel.initializeCancelOrderReasons();
      coreBalanceViewModel.initializeExecutorBalance();
      missedOrderViewModel.initializeMissedOrderMessages();
      updateMessageViewModel.initializeUpdateMessages();
      currentCostPollingViewModel.initializeCurrentCostPolling();
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
        List<Pair<Long, Integer>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(1800L, 0));
        pairs.add(new Pair<>(50L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(100L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(200L, 255));
        pairs.add(new Pair<>(50L, 0));
        pairs.add(new Pair<>(150L, 255));
        pairs.add(new Pair<>(50L, 0));
        shakeIt(pairs);
        startService(R.string.offer, R.string.new_order, PendingIntent
            .getActivity(this, 0, new Intent(this, DriverOrderConfirmationActivity.class), 0));
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        playSound(R.raw.accept_offer);
        shakeIt(Arrays.asList(
            new Pair<>(100L, 255),
            new Pair<>(50L, 0),
            new Pair<>(50L, 255),
            new Pair<>(50L, 0),
            new Pair<>(50L, 255),
            new Pair<>(50L, 0),
            new Pair<>(100L, 255),
            new Pair<>(50L, 0),
            new Pair<>(50L, 255),
            new Pair<>(50L, 0),
            new Pair<>(100L, 255)
        ));
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
    shakeIt(Arrays.asList(
        new Pair<>(50L, 255),
        new Pair<>(50L, 0),
        new Pair<>(100L, 255),
        new Pair<>(50L, 0),
        new Pair<>(150L, 255),
        new Pair<>(50L, 0),
        new Pair<>(200L, 255),
        new Pair<>(50L, 0)
    ));
    if (notificationManager != null) {
      Builder builder = new Builder(this, "state_channel")
          .setContentText(getString(R.string.missed_order))
          .setContentTitle(message)
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

  private void shakeIt(@NonNull List<Pair<Long, Integer>> patternItems) {
    if (shakeItPlayer == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    shakeItPlayer.shakeIt(patternItems);
  }
}
