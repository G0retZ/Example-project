package com.cargopull.executor_driver.entity;

import androidx.annotation.Nullable;

/**
 * Взимоисключающие состояния водителя.
 */
public enum ExecutorState {

  /**
   * Заблокирован. В этом состоянии водителю необходимо обратиться в офис для снятия блокировки.
   */
  BLOCKED,

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
   * Водитель "принимает срочный заказ на выполнение". В этом состоянии водитель принимает решение
   * по полученному заказу.
   */
  DRIVER_ORDER_CONFIRMATION,

  /**
   * Водитель "принимает предзаказ на выполнение". В этом состоянии водитель принимает решение по
   * полученному заказу.
   */
  DRIVER_PRELIMINARY_ORDER_CONFIRMATION,

  /**
   * Водитель "ожидает подтверждения клиента". В этом состоянии водитель ожидает подтверждение
   * заказа клиентом.
   */
  CLIENT_ORDER_CONFIRMATION,

  /**
   * Водитель "на пути к клиенту". В этом состоянии водитель движется на встречу с клиентом.
   */
  MOVING_TO_CLIENT,

  /**
   * Водитель "ожидает выхода клиента". В этом состоянии водитель прибыл на место и ждет на встречи
   * с клиентом.
   */
  WAITING_FOR_CLIENT,

  /**
   * Водитель "выполняет заказ". В этом состоянии водитель выполняет заказ клиента.
   */
  ORDER_FULFILLMENT,

  /**
   * Водитель "принимает оплату". В этом состоянии водитель расчитывается с клиентом.
   */
  PAYMENT_CONFIRMATION;

  @Nullable
  private String data;
  private long customerTimer;

  @Nullable
  public String getData() {
    return data;
  }

  public void setData(@Nullable String data) {
    this.data = data;
  }

  public long getCustomerTimer() {
    return customerTimer;
  }

  public void setCustomerTimer(long customerTimer) {
    this.customerTimer = customerTimer;
  }
}
