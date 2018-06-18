package com.fasten.executor_driver.presentation.executorstate;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при изменении статуса исполнителя.
 */
@StringDef({
    ExecutorStateNavigate.SERVER_DATA_ERROR,
    ExecutorStateNavigate.MAP_SHIFT_CLOSED,
    ExecutorStateNavigate.MAP_SHIFT_OPENED,
    ExecutorStateNavigate.MAP_ONLINE,
    ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION,
    ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION,
    ExecutorStateNavigate.MOVING_TO_CLIENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ExecutorStateNavigate {

  // Переход к решению проблем сети.
  String SERVER_DATA_ERROR = "to.ServerDataError";
  // Переход к карте.
  String MAP_SHIFT_CLOSED = "to.Map.Shift.Closed";
  // Переход к карте.
  String MAP_SHIFT_OPENED = "to.Map.Shift.Opened";
  // Переход к получению заказов.
  String MAP_ONLINE = "to.Map.Online";
  // Переход к исполнению заказа.
  String DRIVER_ORDER_CONFIRMATION = "to.Driver.Order.Confirmation";
  // Переход к ожиданию подтверждения клиентом.
  String CLIENT_ORDER_CONFIRMATION = "to.Client.Order.Confirmation";
  // Переход к движению к клиенту.
  String MOVING_TO_CLIENT = "to.Moving.To.Client";
  // Переход к движению к клиенту.
  String WAITING_FOR_CLIENT = "to.Waiting.For.Client";
  // Переход к выполнению заказа.
  String ORDER_FULFILLMENT = "to.Order.Fulfillment";
}
