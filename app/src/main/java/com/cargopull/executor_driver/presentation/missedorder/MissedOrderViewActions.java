package com.cargopull.executor_driver.presentation.missedorder;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна упущенного заказа.
 */
public interface MissedOrderViewActions {

  /**
   * Показать сообщение об упущенном заказе.
   *
   * @param message текст сообщения
   */
  void showMissedOrderMessage(@NonNull String message);
}
