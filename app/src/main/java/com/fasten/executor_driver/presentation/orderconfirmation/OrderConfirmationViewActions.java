package com.fasten.executor_driver.presentation.orderconfirmation;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OrderConfirmationViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showDriverOrderConfirmationPending(boolean pending);

  /**
   * Задействовать кнопку отказа.
   *
   * @param enable - задействовать или нет?
   */
  void enableDeclineButton(boolean enable);

  /**
   * Задействовать кнопку приема.
   *
   * @param enable - задействовать или нет?
   */
  void enableAcceptButton(boolean enable);
}
