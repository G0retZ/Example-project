package com.fasten.executor_driver.presentation.ordertime;

/**
 * Действия для смены состояния вида текущего времени заказа.
 */
public interface OrderTimeViewActions {

  /**
   * Задать общее время заказа.
   *
   * @param currentSeconds - текущее время заказа в секундах.
   */
  void setOrderTimeText(long currentSeconds);

  /**
   * Показать ошибку данных сервера.
   */
  void showOrderTimeServerDataError();
}
