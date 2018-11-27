package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.FragmentViewActions;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OrdersHistoryHeaderViewActions extends FragmentViewActions {

  /**
   * Вернуть необходимость отображения копеек/центов.
   */
  boolean isShowCents();

  /**
   * Вернуть формат отображения валюты.
   */
  @NonNull
  String getCurrencyFormat();
}
