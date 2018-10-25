package com.cargopull.executor_driver.application;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionNavigate;
import com.cargopull.executor_driver.utils.Consumer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

public class NavigationMapperImpl implements NavigationMapper {

  /**
   * Реестр активити, разбитых по группам, чтобы исключить нежелательные переходы по навигации.
   */
  private static final HashMap<String, List<Class<? extends Activity>>> statusGroups = new HashMap<>();

  static {
    statusGroups.put(ServerConnectionNavigate.AUTHORIZE, Arrays.asList(
        LoginActivity.class, PasswordActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.BLOCKED, Arrays.asList(
        BlockedActivity.class, GeolocationPermissionActivity.class,
        BalanceActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_CLOSED, Arrays.asList(
        MapActivity.class, GeolocationPermissionActivity.class,
        BalanceActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_OPENED, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class, ServicesActivity.class,
        GeolocationPermissionActivity.class, BalanceActivity.class,
        SelectedVehicleOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_ONLINE, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class,
        GeolocationPermissionActivity.class, BalanceActivity.class,
        SelectedVehicleOptionsActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION, Arrays.asList(
        DriverOrderConfirmationActivity.class, GeolocationPermissionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION, Arrays.asList(
        DriverPreOrderConfirmationActivity.class, GeolocationPermissionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION, Arrays.asList(
        ClientOrderConfirmationActivity.class, GeolocationPermissionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MOVING_TO_CLIENT, Arrays.asList(
        MovingToClientActivity.class, MovingToClientDetailsActivity.class,
        MovingToClientRouteActivity.class, GeolocationPermissionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.WAITING_FOR_CLIENT, Arrays.asList(
        WaitingForClientActivity.class, WaitingForClientRouteActivity.class,
        GeolocationPermissionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.ORDER_FULFILLMENT, Arrays.asList(
        OrderFulfillmentActivity.class, OrderFulfillmentDetailsActivity.class,
        OrderRouteActivity.class, GeolocationPermissionActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.PAYMENT_CONFIRMATION, Arrays.asList(
        OrderCostDetailsActivity.class, OrderCostDetailsOrderDetailsActivity.class,
        OrderCostDetailsRouteActivity.class, GeolocationPermissionActivity.class
    ));
    statusGroups.put(PreOrderNavigate.ORDER_APPROVAL, Arrays.asList(
        DriverPreOrderBookingActivity.class, DriverOrderConfirmationActivity.class,
        DriverPreOrderConfirmationActivity.class, ClientOrderConfirmationActivity.class,
        MovingToClientActivity.class, MovingToClientDetailsActivity.class,
        MovingToClientRouteActivity.class, WaitingForClientActivity.class,
        WaitingForClientRouteActivity.class, OrderFulfillmentActivity.class,
        OrderFulfillmentDetailsActivity.class, OrderRouteActivity.class,
        OrderCostDetailsActivity.class, OrderCostDetailsOrderDetailsActivity.class,
        OrderCostDetailsRouteActivity.class, GeolocationPermissionActivity.class
    ));
  }

  @NonNull
  private final RingTonePlayer ringTonePlayer;
  @NonNull
  private final ShakeItPlayer shakeItPlayer;
  private boolean reset = false;
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
  public NavigationMapperImpl(@NonNull RingTonePlayer ringTonePlayer,
      @NonNull ShakeItPlayer shakeItPlayer) {
    this.ringTonePlayer = ringTonePlayer;
    this.shakeItPlayer = shakeItPlayer;
  }

  @Override
  @NonNull
  public Consumer<Activity> navigateTo(
      @NonNull @ExecutorStateNavigate @GeoLocationNavigate String destination) {
    switch (destination) {
      case CommonNavigate.NO_CONNECTION:
        reset = true;
        break;
      case GeoLocationNavigate.RESOLVE_GEO_PERMISSIONS:
        goToGeoResolver = true;
        return this::tryToResolveGeo;
      case PreOrderNavigate.ORDER_APPROVAL:
        if (lastRouteAction == null || !lastRouteAction.equals(CommonNavigate.SERVER_DATA_ERROR)) {
          return this::tryToResolvePreOrder;
        }
        break;
      default:
        if (lastRouteAction == null || !lastRouteAction.equals(CommonNavigate.SERVER_DATA_ERROR)) {
          if (splashRouteAction != null && (destination.equals(ExecutorStateNavigate.MAP_ONLINE)
              || destination.equals(ExecutorStateNavigate.MAP_SHIFT_OPENED))) {
            if (ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION.equals(splashRouteAction)
                || ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION
                .equals(splashRouteAction)) {
              ringTonePlayer.playRingTone(R.raw.decline_offer);
              shakeItPlayer.shakeIt(R.raw.decline_offer_vibro);
            }
          }
          splashRouteAction = lastRouteAction = destination;
        }
        return this::tryToNavigate;
    }
    return activity -> {
    };
  }

  @Override
  @NonNull
  public Consumer<Activity> navigateToRecent() {
    return activity -> {
      tryToNavigate(activity);
      tryToResolveGeo(activity);
    };
  }

  private void tryToResolveGeo(@NonNull Activity activity) {
    if (goToGeoResolver) {
      activity.startActivity(
          new Intent(activity, GeolocationPermissionActivity.class)
      );
      goToGeoResolver = false;
    }
  }

  private void tryToResolvePreOrder(@NonNull Activity currentActivity) {
    // Получаем группу активити по направлению, с которых нельзя просто так перебрасывать.
    List<Class<? extends Activity>> group = statusGroups.get(PreOrderNavigate.ORDER_APPROVAL);
    // Если такая группа есть и в ней есть текущая активити.
    if (!reset && group != null && group.contains(currentActivity.getClass())) {
      // Никуда не переходим.
      return;
    }
    currentActivity.startActivity(
        new Intent(currentActivity, DriverPreOrderBookingActivity.class)
    );
  }

  private void tryToNavigate(@NonNull Activity activity) {
    // Если это сплеш-скрин, то планируем ему последний переход по статусу, если такой был.
    if (activity instanceof SplashScreenActivity) {
      lastRouteAction = splashRouteAction;
    }
    if (lastRouteAction == null) {
      return;
    }
    // Получаем группу активити по направлению, с которых нельзя просто так перебрасывать.
    List<Class<? extends Activity>> group = statusGroups.get(lastRouteAction);
    // Если такая группа есть и в ней есть текущая активити.
    if (!reset && group != null && group.contains(activity.getClass())) {
      // Если текущая активити является основной в группе, то обнуляем направление навигации.
      lastRouteAction = group.get(0) == activity.getClass() ? null : lastRouteAction;
      // Никуда не переходим.
      return;
    }
    switch (lastRouteAction) {
      case ServerConnectionNavigate.AUTHORIZE:
        activity.startActivity(
            new Intent(activity, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ServerConnectionNavigate.VERSION_DEPRECATED:
        break;
      case CommonNavigate.SERVER_DATA_ERROR:
        return;
      case CommonNavigate.EXIT:
        return;
      case ExecutorStateNavigate.BLOCKED:
        activity.startActivity(
            new Intent(activity, BlockedActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        activity.startActivity(
            new Intent(activity, MapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        activity.startActivity(
            new Intent(activity, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        activity.startActivity(
            new Intent(activity, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
        activity.startActivity(
            new Intent(activity, DriverOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION:
        activity.startActivity(
            new Intent(activity, DriverPreOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        activity.startActivity(
            new Intent(activity, ClientOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MOVING_TO_CLIENT:
        activity.startActivity(
            new Intent(activity, MovingToClientActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.WAITING_FOR_CLIENT:
        activity.startActivity(
            new Intent(activity, WaitingForClientActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.ORDER_FULFILLMENT:
        activity.startActivity(
            new Intent(activity, OrderFulfillmentActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.PAYMENT_CONFIRMATION:
        activity.startActivity(
            new Intent(activity, OrderCostDetailsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      default:
        new Builder(activity)
            .setTitle(R.string.error)
            .setMessage(R.string.status_unknown)
            .setCancelable(false)
            .setPositiveButton(activity.getString(android.R.string.ok), null)
            .create()
            .show();
        return;
    }
    // Если переход сработал, то обнуляем направление. Если нет, то следующее активити попробует его обработать
    lastRouteAction = null;
    // Если переход сработал, то сбрасываем флаг сброса экрана
    reset = false;
  }
}
