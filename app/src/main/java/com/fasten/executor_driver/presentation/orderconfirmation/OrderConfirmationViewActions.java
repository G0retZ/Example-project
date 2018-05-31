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
   * Показать ошибку доступности заказов.
   *
   * @param show - показать или нет?
   */
  void showOrderAvailabilityError(boolean show);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showNetworkErrorMessage(boolean show);

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
