package com.cargopull.executor_driver.presentation.menu;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню.
 */
@StringDef({
    MenuNavigate.BALANCE,
    MenuNavigate.PRE_ORDERS,
    MenuNavigate.NIGHT_MODE,
    MenuNavigate.ABOUT
})
@Retention(RetentionPolicy.SOURCE)
public @interface MenuNavigate {

  // Переход к балансу.
  String BALANCE = "Menu.to.Balance";

  // Переход к балансу.
  String PRE_ORDERS = "Menu.to.PreOrders";

  // Переход к настройке ночного режима.
  String NIGHT_MODE = "Menu.to.NightMode";

  // Переход к информации о приложении.
  String ABOUT = "Menu.to.About";
}
