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
  @SuppressWarnings("unused")
  ORDER_CONFIRMATION,
  /**
   * Водитель "на пути к точке погрузки". В этом состоянии водитель направляется к точке погрузки.
   */
  IN_PROGRESS;

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
