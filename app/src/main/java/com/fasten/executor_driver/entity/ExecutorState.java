package com.fasten.executor_driver.entity;

/**
 * Взимоисключающие состояния водителя.
 */
public enum ExecutorState {
  /**
   * Водитель не авторизован. В этом состоянии водитель должен авторизоваться, чтобы войти в
   * приложение.
   */
  UNAUTHORIZED,
  /**
   * Смена закрыта. В этом состоянии водитель не получает заказов, и не занимает никакой ТС.
   */
  @SuppressWarnings("unused")CLOSED_SHIFT,
  /**
   * Смена открыта. В этом состоянии водитель не получает заказов, занимает одну из доступных ТС.
   */
  @SuppressWarnings("unused")OPENED_SHIFT,
  /**
   * Водитель "на линии". В этом состоянии водитель получает заказы для занимаемого ТС.
   */
  @SuppressWarnings("unused")READY_FOR_ORDERS,
  /**
   * Водитель движется к месту погрузки.
   */
  APPROACHING_LOADING_POINT,
  /**
   * Водитель движется к месту разгрузки.
   */
  @SuppressWarnings("unused")APPROACHING_UNLOADING_POINT
}
