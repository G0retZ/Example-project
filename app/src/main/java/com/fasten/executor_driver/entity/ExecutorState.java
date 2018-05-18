package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;

/**
 * Взимоисключающие состояния водителя.
 */
public enum ExecutorState {
  /**
   * Смена закрыта. В этом состоянии водитель не получает заказов, и не занимает никакой ТС.
   */
  SHIFT_CLOSED,
  /**
   * Смена открыта. В этом состоянии водитель не получает заказов, занимает одну из доступных ТС.
   */
  SHIFT_OPENED,
  /**
   * Водитель "на линии". В этом состоянии водитель получает заказы для занимаемого ТС.
   */
  ONLINE,
  /**
   * Водитель "принимает заказ". В этом состоянии водитель принимает решение по полученному заказу.
   */
  DRIVER_ORDER_CONFIRMATION,
  /**
   * Водитель "ожидает подтверждения клиента". В этом состоянии водитель ожидает подтверждение заказа клиентом.
   */
  CLIENT_ORDER_CONFIRMATION,
  /**
   * Водитель "на пути к клиенту". В этом состоянии водитель движется на встречу с клиентом.
   */
  MOVING_TO_CLIENT,
  /**
   * Водитель "ожидает выхода клиента". В этом состоянии водитель прибыл на место и ждет на встречи с клиентом.
   */
  WAITING_FOR_CLIENT;

  @Nullable
  private String data;

  @Nullable
  public String getData() {
    return data;
  }

  public void setData(@Nullable String data) {
    this.data = data;
  }
}
