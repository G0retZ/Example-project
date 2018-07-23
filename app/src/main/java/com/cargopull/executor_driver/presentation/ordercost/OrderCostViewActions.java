package com.cargopull.executor_driver.presentation.ordercost;

/**
 * Действия для смены состояния вида текущей стоимости заказа.
 */
public interface OrderCostViewActions {

  /**
   * Задать текст стоимости.
   *
   * @param currentCost - текущая стоимость заказа.
   */
  void setOrderCostText(long currentCost);
}
