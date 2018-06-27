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
  String SERVER_DATA_ERROR = "ExecutorState.to.ServerDataError";
  // Переход к карте.
  String MAP_SHIFT_CLOSED = "ExecutorState.to.MapShiftClosed";
  // Переход к карте.
  String MAP_SHIFT_OPENED = "ExecutorState.to.MapShiftOpened";
  // Переход к получению заказов.
  String MAP_ONLINE = "ExecutorState.to.MapOnline";
  // Переход к исполнению заказа.
  String DRIVER_ORDER_CONFIRMATION = "ExecutorState.to.DriverOrderConfirmation";
  // Переход к ожиданию подтверждения клиентом.
  String CLIENT_ORDER_CONFIRMATION = "ExecutorState.to.ClientOrderConfirmation";
  // Переход к движению к клиенту.
  String MOVING_TO_CLIENT = "ExecutorState.to.MovingToClient";
  // Переход к движению к клиенту.
  String WAITING_FOR_CLIENT = "ExecutorState.to.WaitingForClient";
  // Переход к выполнению заказа.
  String ORDER_FULFILLMENT = "ExecutorState.to.OrderFulfillment";
}
