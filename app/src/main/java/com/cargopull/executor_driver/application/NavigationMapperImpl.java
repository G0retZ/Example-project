package com.cargopull.executor_driver.application;

import android.app.AlertDialog.Builder;
import android.content.Context;
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
  private static final HashMap<String, List<Class<? extends Context>>> statusGroups = new HashMap<>();

  static {
    statusGroups.put(ServerConnectionNavigate.AUTHORIZE, Arrays.asList(
        LoginActivity.class, PasswordActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.BLOCKED, Arrays.asList(
        BlockedActivity.class, GeolocationPermissionActivity.class,
        BalanceActivity.class, OrdersHistoryActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_CLOSED, Arrays.asList(
        MapActivity.class, GeolocationPermissionActivity.class,
        BalanceActivity.class, OrdersHistoryActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_SHIFT_OPENED, Arrays.asList(
        OnlineActivity.class, VehicleOptionsActivity.class, GeolocationPermissionActivity.class,
        BalanceActivity.class, CurrentVehicleOptionsActivity.class, OrdersHistoryActivity.class
    ));
    statusGroups.put(ExecutorStateNavigate.MAP_ONLINE, Arrays.asList(
        OnlineActivity.class, GeolocationPermissionActivity.class, BalanceActivity.class,
        OrdersHistoryActivity.class
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
  public Consumer<Context> navigateTo(
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
          lastRouteAction = destination;
          return this::tryToNavigate;
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
  public Consumer<Context> navigateToRecent() {
    return context -> {
      tryToNavigate(context);
      tryToResolveGeo(context);
    };
  }

  private void tryToResolveGeo(@NonNull Context context) {
    if (goToGeoResolver) {
      context.startActivity(
          new Intent(context, GeolocationPermissionActivity.class)
      );
      goToGeoResolver = false;
    }
  }

  private void tryToNavigate(@NonNull Context context) {
    // Если это сплеш-скрин, то планируем ему последний переход по статусу, если такой был.
    if (context instanceof SplashScreenActivity) {
      lastRouteAction = splashRouteAction;
    }
    if (lastRouteAction == null) {
      return;
    }
    // Получаем группу активити по направлению, с которых нельзя просто так перебрасывать.
    List<Class<? extends Context>> group = statusGroups.get(lastRouteAction);
    // Если такая группа есть и в ней есть текущая активити.
    if (!reset && group != null && group.contains(context.getClass())) {
      // Если текущая активити является основной в группе, то обнуляем направление навигации.
      lastRouteAction = group.get(0) == context.getClass() ? null : lastRouteAction;
      // Никуда не переходим.
      return;
    }
    switch (lastRouteAction) {
      case ServerConnectionNavigate.AUTHORIZE:
        context.startActivity(
            new Intent(context, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ServerConnectionNavigate.VERSION_DEPRECATED:
        break;
      case CommonNavigate.SERVER_DATA_ERROR:
        return;
      case CommonNavigate.EXIT:
        return;
      case PreOrderNavigate.ORDER_APPROVAL:
        context.startActivity(
            new Intent(context, DriverPreOrderBookingActivity.class)
        );
        return;
      case ExecutorStateNavigate.BLOCKED:
        context.startActivity(
            new Intent(context, BlockedActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        context.startActivity(
            new Intent(context, MapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        context.startActivity(
            new Intent(context, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        context.startActivity(
            new Intent(context, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION:
        context.startActivity(
            new Intent(context, DriverOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION:
        context.startActivity(
            new Intent(context, DriverPreOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION:
        context.startActivity(
            new Intent(context, ClientOrderConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.MOVING_TO_CLIENT:
        context.startActivity(
            new Intent(context, MovingToClientActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.WAITING_FOR_CLIENT:
        context.startActivity(
            new Intent(context, WaitingForClientActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.ORDER_FULFILLMENT:
        context.startActivity(
            new Intent(context, OrderFulfillmentActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case ExecutorStateNavigate.PAYMENT_CONFIRMATION:
        context.startActivity(
            new Intent(context, OrderCostDetailsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      default:
        new Builder(context)
            .setTitle(R.string.error)
            .setMessage(R.string.status_unknown)
            .setCancelable(false)
            .setPositiveButton(context.getString(android.R.string.ok), null)
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
