package com.fasten.executor_driver.presentation.ordercost;

import android.support.annotation.StringRes;

/**
 * Действия для смены состояния вида текущей стоимости заказа.
 */
interface OrderCostViewActions {

  /**
   * Задать текст стоимости.
   *
   * @param textId - ИД ресурса текста
   * @param currentCost - текущая стоимость заказа.
   */
  void setOrderCostText(@StringRes int textId, int currentCost);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showOrderCostNetworkErrorMessage(boolean show);
}
