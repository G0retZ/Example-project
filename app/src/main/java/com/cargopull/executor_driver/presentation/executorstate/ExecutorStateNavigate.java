package com.cargopull.executor_driver.presentation.executorstate;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при изменении статуса исполнителя.
 */
@StringDef({
    ExecutorStateNavigate.BLOCKED,
    ExecutorStateNavigate.MAP_SHIFT_CLOSED,
    ExecutorStateNavigate.MAP_SHIFT_OPENED,
    ExecutorStateNavigate.MAP_ONLINE,
    ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION,
    ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
    ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION,
    ExecutorStateNavigate.MOVING_TO_CLIENT,
    ExecutorStateNavigate.WAITING_FOR_CLIENT,
    ExecutorStateNavigate.ORDER_FULFILLMENT,
    ExecutorStateNavigate.PAYMENT_CONFIRMATION
})
@Retention(RetentionPolicy.SOURCE)
public @interface ExecutorStateNavigate {

  // Переход к экрану бллокировки.
  String BLOCKED = "ExecutorState.to.Blocked";
  // Переход к карте.
  String MAP_SHIFT_CLOSED = "ExecutorState.to.MapShiftClosed";
  // Переход к карте.
  String MAP_SHIFT_OPENED = "ExecutorState.to.MapShiftOpened";
  // Переход к получению заказов.
  String MAP_ONLINE = "ExecutorState.to.MapOnline";
  // Переход к подтверждению заказа.
  String DRIVER_ORDER_CONFIRMATION = "ExecutorState.to.DriverOrderConfirmation";
  // Переход к подтверждению предварительного заказа.
  String DRIVER_PRELIMINARY_ORDER_CONFIRMATION = "ExecutorState.to.DriverPreliminaryOrderConfirmation";
  // Переход к ожиданию подтверждения клиентом.
  String CLIENT_ORDER_CONFIRMATION = "ExecutorState.to.ClientOrderConfirmation";
  // Переход к движению к клиенту.
  String MOVING_TO_CLIENT = "ExecutorState.to.MovingToClient";
  // Переход к движению к клиенту.
  String WAITING_FOR_CLIENT = "ExecutorState.to.WaitingForClient";
  // Переход к выполнению заказа.
  String ORDER_FULFILLMENT = "ExecutorState.to.OrderFulfillment";
  // Переход к расчету с клиентом.
  String PAYMENT_CONFIRMATION = "ExecutorState.to.PaymentAcceptance";
}
