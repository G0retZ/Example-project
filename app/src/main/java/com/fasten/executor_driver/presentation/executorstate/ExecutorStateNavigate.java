package com.fasten.executor_driver.presentation.executorstate;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при старте приложения.
 */
@StringDef({
    ExecutorStateNavigate.NO_NETWORK,
    ExecutorStateNavigate.AUTHORIZE,
    ExecutorStateNavigate.MAP_SHIFT_CLOSED,
    ExecutorStateNavigate.MAP_SHIFT_OPENED,
    ExecutorStateNavigate.MAP_ONLINE,
    ExecutorStateNavigate.ORDER_CONFIRMATION,
    ExecutorStateNavigate.APPROACHING_LOAD_POINT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ExecutorStateNavigate {

  // Переход к решению проблем сети.
  String NO_NETWORK = "to.NoNetwork";
  // Переход к авторизации.
  String AUTHORIZE = "to.Authorization";
  // Переход к карте.
  String MAP_SHIFT_CLOSED = "to.Map.Shift.Closed";
  // Переход к карте.
  String MAP_SHIFT_OPENED = "to.Map.Shift.Opened";
  // Переход к получению заказов.
  String MAP_ONLINE = "to.Map.Online";
  // Переход к исполнению заказа.
  String ORDER_CONFIRMATION = "to.Order.Confirmation";
  // Переход к движению к точке погрузки.
  String APPROACHING_LOAD_POINT = "to.Approaching.Load.Point";
}
