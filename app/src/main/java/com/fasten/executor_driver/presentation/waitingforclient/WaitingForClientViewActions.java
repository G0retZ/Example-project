package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface WaitingForClientViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showWaitingForClientPending(boolean pending);

  /**
   * Показать рассчет стоимости.
   *
   * @param priceText - оценка стоимости заказа
   */
  void showEstimatedPrice(@NonNull String priceText);

  /**
   * Показать опции, необходимые для принятия заказа.
   *
   * @param options - список опций, требуемых для выполнения заказа
   */
  void showOrderOptionsRequirements(@NonNull String options);

  /**
   * Показать комментарий к заказу.
   *
   * @param comment - адрес
   */
  void showComment(@NonNull String comment);

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
}
