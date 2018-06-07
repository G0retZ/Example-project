package com.fasten.executor_driver.presentation.ordercost;

/**
 * Действия для смены состояния вида текущей стоимости заказа.
 */
public interface OrderCostViewActions {

  /**
   * Задать текст стоимости.
   *
   * @param currentCost - текущая стоимость заказа.
   */
  void setOrderCostText(int currentCost);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showOrderCostNetworkErrorMessage(boolean show);
}
