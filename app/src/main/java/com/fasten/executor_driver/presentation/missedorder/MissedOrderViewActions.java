package com.fasten.executor_driver.presentation.missedorder;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна упущенного заказа.
 */
interface MissedOrderViewActions {

  /**
   * Показать сообщение об упущенном заказе.
   *
   * @param message текс сообщения
   */
  void showMissedOrderMessage(@NonNull String message);
}
