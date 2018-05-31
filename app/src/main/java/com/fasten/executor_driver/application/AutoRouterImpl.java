package com.fasten.executor_driver.application;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewActions;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationNavigate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

public class AutoRouterImpl implements ActivityLifecycleCallbacks, AutoRouter,
    ExecutorStateViewActions {

  /**
   * Реестр активити, разбитых по группам, чтобы исключить нежелательные переходы по навигации.
   */
  private static final HashMap<String, List<Class<? extends Activity>>> statusGroups = new HashMap<>();

  static {
    statusGroups.put(ExecutorStateNavigate.AUTHORIZE, Arrays.asList(
        LoginActivity.class, PasswordActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_CLOSED, Arrays.asList(
        MapActivity.class, GeolocationResolutionActivity.class, MenuActivity.class,
        BalanceActivity.class, PaymentOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_OPENED, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class, ServicesActivity.class,
        GeolocationResolutionActivity.class, MenuActivity.class, BalanceActivity.class,
        PaymentOptionsActivity.class, SelectedVehicleOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_ONLINE, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class,
        GeolocationResolutionActivity.class, MenuActivity.class, BalanceActivity.class,
        PaymentOptionsActivity.class, SelectedVehicleOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION, Arrays.asList(
        DriverOrderConfirmationActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION, Arrays.asList(
        ClientOrderConfirmationActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MOVING_TO_CLIENT, Arrays.asList(
        MovingToClientActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.WAITING_FOR_CLIENT, Arrays.asList(
        WaitingForClientActivity.class, GeolocationResolutionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.ORDER_FULFILLMENT, Arrays.asList(
        OrderFulfillmentActivity.class, OrderFulfillmentDetailsActivity.class,
        GeolocationResolutionActivity.class
    ));
  }

  @Nullable
  private Activity currentActivity;
  @Nullable
  @ExecutorStateNavigate
  @GeoLocationNavigate
  private String lastRouteAction;
  private boolean goToGeoResolver;
  @Nullable
  private Runnable messageRunnable;

  @Inject
  public AutoRouterImpl() {
  }

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  }

  @Override
  public void onActivityStarted(Activity activity) {
    currentActivity = activity;
    tryToNavigate();
    tryToResolveGeo();
    if (messageRunnable != null) {
      messageRunnable.run();
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
    if (destination.equals(GeoLocationNavigate.RESOLVE_GEO_PROBLEM)) {
      goToGeoResolver = true;
      tryToResolveGeo();
    } else {
      lastRouteAction = destination;
      tryToNavigate();
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
    // Если такой группы нет или в ней нет текущей активити, то навигируем.
    if (group != null && group.contains(currentActivity.getClass())) {
      lastRouteAction = group.get(0) == currentActivity.getClass() ? null : lastRouteAction;
      return;
    }
    switch (lastRouteAction) {
      case ExecutorStateNavigate.NO_NETWORK:
        new Builder(currentActivity)
            .setTitle(R.string.error)
            .setMessage("Без сети не работаем!")
            .setCancelable(false)
            .setPositiveButton(
                currentActivity.getString(android.R.string.ok),
                (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
            )
            .create()
            .show();
        break;
      case ExecutorStateNavigate.AUTHORIZE:
        currentActivity.startActivity(
            new Intent(currentActivity, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
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
      default:
        new Builder(currentActivity)
            .setTitle(R.string.error)
            .setMessage("Неизвестный статус!")
            .setCancelable(false)
            .setPositiveButton(
                currentActivity.getString(android.R.string.ok),
                (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
            )
            .create()
            .show();
        return;
    }
    // Если переход сработал, то обнуляем направление. Если нет, то следующее активити попробует его обработать
    lastRouteAction = null;
  }

  @Override
  public void showMessage(@NonNull String message) {
    messageRunnable = () -> {
      if (currentActivity != null) {
        new Builder(currentActivity)
            .setTitle(R.string.information)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(currentActivity.getString(android.R.string.ok), null)
            .create()
            .show();
        messageRunnable = null;
      }
    };
  }
}
