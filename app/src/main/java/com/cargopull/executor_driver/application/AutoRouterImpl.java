package com.cargopull.executor_driver.application;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionNavigate;
import com.cargopull.executor_driver.utils.Pair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

public class AutoRouterImpl implements ActivityLifecycleCallbacks, AutoRouter {

  /**
   * Реестр активити, разбитых по группам, чтобы исключить нежелательные переходы по навигации.
   */
  private static final HashMap<String, List<Class<? extends Activity>>> statusGroups = new HashMap<>();

  static {
    statusGroups.put(ServerConnectionNavigate.AUTHORIZE, Arrays.asList(
        LoginActivity.class, PasswordActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_CLOSED, Arrays.asList(
        MapActivity.class, GeolocationResolutionActivity.class,
        BalanceActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_OPENED, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class, ServicesActivity.class,
        GeolocationResolutionActivity.class, BalanceActivity.class,
        SelectedVehicleOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_ONLINE, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class,
        GeolocationResolutionActivity.class, BalanceActivity.class,
        SelectedVehicleOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION, Arrays.asList(
        DriverOrderConfirmationActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION, Arrays.asList(
        ClientOrderConfirmationActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MOVING_TO_CLIENT, Arrays.asList(
        MovingToClientActivity.class, MovingToClientDetailsActivity.class,
        MovingToClientRouteActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.WAITING_FOR_CLIENT, Arrays.asList(
        WaitingForClientActivity.class, WaitingForClientRouteActivity.class,
        GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.ORDER_FULFILLMENT, Arrays.asList(
        OrderFulfillmentActivity.class, OrderFulfillmentDetailsActivity.class,
        OrderRouteActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.PAYMENT_CONFIRMATION, Arrays.asList(
        OrderCostDetailsActivity.class, OrderCostDetailsOrderDetailsActivity.class,
        OrderCostDetailsRouteActivity.class, GeolocationResolutionActivity.class
    ));
  }

  @NonNull
  private final RingTonePlayer ringTonePlayer;
  @NonNull
  private final ShakeItPlayer shakeItPlayer;
  private boolean reset = false;
  @Nullable
  private Activity currentActivity;
  @Nullable
  @ExecutorStateNavigate
  @GeoLocationNavigate
  private String lastRouteAction;
  @Nullable
  @ExecutorStateNavigate
  @GeoLocationNavigate
  private String splashRouteAction;
  private boolean goToGeoResolver;

  @Inject
  public AutoRouterImpl(@NonNull RingTonePlayer ringTonePlayer,
      @NonNull ShakeItPlayer shakeItPlayer) {
    this.ringTonePlayer = ringTonePlayer;
    this.shakeItPlayer = shakeItPlayer;
  }

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  }

  @Override
  public void onActivityStarted(Activity activity) {
    currentActivity = activity;
    // Если это сплеш-скрин, то планируем ему последний переход по статусу, если такой был.
    if (activity instanceof SplashScreenActivity) {
      lastRouteAction = splashRouteAction;
    }
    tryToNavigate();
    tryToResolveGeo();
  }

  @Override
  public void onActivityResumed(Activity activity) {
  }

  @Override
  public void onActivityPaused(Activity activity) {
  }

  @Override
  public void onActivityStopped(Activity activity) {
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
    if (currentActivity == activity) {
      currentActivity = null;
    }
  }

  @Override
  public void navigateTo(@NonNull @ExecutorStateNavigate @GeoLocationNavigate String destination) {
    switch (destination) {
      case CommonNavigate.NO_CONNECTION:
        reset = true;
        break;
      case GeoLocationNavigate.RESOLVE_GEO_PROBLEM:
        goToGeoResolver = true;
        tryToResolveGeo();
        break;
      default:
        if (lastRouteAction == null || !lastRouteAction.equals(CommonNavigate.SERVER_DATA_ERROR)) {
          if (splashRouteAction != null && destination.equals(ExecutorStateNavigate.MAP_ONLINE)) {
            switch (splashRouteAction) {
              case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
                ringTonePlayer.playRingTone(R.raw.decline_offer);
                shakeItPlayer.shakeIt(Arrays.asList(
                    new Pair<>(50L, 255),
                    new Pair<>(50L, 0),
                    new Pair<>(100L, 255),
                    new Pair<>(50L, 0),
                    new Pair<>(150L, 255),
                    new Pair<>(50L, 0),
                    new Pair<>(250L, 255)
                ));
                break;
              default:
            }
          }
          splashRouteAction = lastRouteAction = destination;
        }
        tryToNavigate();
        break;
    }
  }

  private void tryToResolveGeo() {
    if (currentActivity != null && goToGeoResolver) {
      currentActivity.startActivity(
          new Intent(currentActivity, GeolocationResolutionActivity.class)
      );
      goToGeoResolver = false;
    }
  }

  private void tryToNavigate() {
    if (currentActivity == null || lastRouteAction == null) {
      return;
    }
    // Получаем группу активити по направлению, с которых нельзя просто так перебрасывать.
    List<Class<? extends Activity>> group = statusGroups.get(lastRouteAction);
    // Если такая группа есть и в ней есть текущая активити, то никуда не навигируем.
    if (!reset && group != null && group.contains(currentActivity.getClass())) {
      // Если текущая активити является основной в группе, то обнуляем направление навигации.
      lastRouteAction = group.get(0) == currentActivity.getClass() ? null : lastRouteAction;
      // Так как никуда не переходим, то пытаемся отобразить сообщения, если есть.
      return;
    }
    switch (lastRouteAction) {
      case ServerConnectionNavigate.AUTHORIZE:
        currentActivity.startActivity(
            new Intent(currentActivity, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ServerConnectionNavigate.VERSION_DEPRECATED:
        break;
      case CommonNavigate.SERVER_DATA_ERROR:
        return;
      case CommonNavigate.EXIT:
        return;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        currentActivity.startActivity(
            new Intent(currentActivity, MapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        currentActivity.startActivity(
            new Intent(currentActivity, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        currentActivity.startActivity(
            new Intent(currentActivity, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
        currentActivity.startActivity(
            new Intent(currentActivity, DriverOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        currentActivity.startActivity(
            new Intent(currentActivity, ClientOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MOVING_TO_CLIENT:
        currentActivity.startActivity(
            new Intent(currentActivity, MovingToClientActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.WAITING_FOR_CLIENT:
        currentActivity.startActivity(
            new Intent(currentActivity, WaitingForClientActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.ORDER_FULFILLMENT:
        currentActivity.startActivity(
            new Intent(currentActivity, OrderFulfillmentActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.PAYMENT_CONFIRMATION:
        currentActivity.startActivity(
            new Intent(currentActivity, OrderCostDetailsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      default:
        if (currentActivity != null) {
          new Builder(currentActivity)
              .setTitle(R.string.error)
              .setMessage(R.string.status_unknown)
              .setCancelable(false)
              .setPositiveButton(currentActivity.getString(android.R.string.ok), null)
              .create()
              .show();
        }
        return;
    }
    // Если переход сработал, то обнуляем направление. Если нет, то следующее активити попробует его обработать
    lastRouteAction = null;
    // Если переход сработал, то сбрасываем флаг сброса экрана
    reset = false;
  }
}
