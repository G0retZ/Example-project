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
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showOrderTimeNetworkErrorMessage(boolean show);
}
