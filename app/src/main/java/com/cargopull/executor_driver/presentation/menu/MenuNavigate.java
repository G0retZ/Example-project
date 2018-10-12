package com.cargopull.executor_driver.presentation.menu;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню.
 */
@StringDef({
    MenuNavigate.BALANCE,
    MenuNavigate.PRE_ORDERS
})
@Retention(RetentionPolicy.SOURCE)
public @interface MenuNavigate {

  // Переход к балансу.
  String BALANCE = "Menu.to.Balance";

  // Переход к балансу.
  String PRE_ORDERS = "Menu.to.PreOrders";
}
