package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.Nullable;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OrderConfirmationViewActions {

  /**
   * Показать индикатор таймаута приниятия решения по заказу. (-1;-1) для заморозки таймера
   *
   * @param timeout - время оставшееся до таймаута
   */
  void showTimeout(long timeout);

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

  /**
   * Показать сообщение об успешном принятии заказа.
   *
   * @param message - текст сообщения или null, если не показывать
   */
  void showAcceptedMessage(@Nullable String message);

  /**
   * Показать сообщение об успешном отказе от заказа.
   *
   * @param message - текст сообщения или null, если не показывать
   */
  void showDeclinedMessage(@Nullable String message);

  /**
   * Показать сообщение о просроченном заказе.
   *
   * @param message - текст сообщения или null, если не показывать
   */
  void showFailedMessage(@Nullable String message);
}
