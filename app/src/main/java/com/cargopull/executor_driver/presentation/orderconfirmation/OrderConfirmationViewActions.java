package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OrderConfirmationViewActions {

  /**
   * Показать индикатор таймаута приниятия решения по заказу. (-1;-1) для заморозки таймера
   *
   * @param progress - сколько процентов осталось до окончания
   * @param timeout - время оставшееся до таймаута
   */
  void showTimeout(int progress, long timeout);

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
   * Показать Сообщение.
   *
   * @param message - текст сообщения или null, если не показывать
   */
  void showBlockingMessage(@Nullable String message);
}
