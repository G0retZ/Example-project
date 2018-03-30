package com.fasten.executor_driver.presentation.menu;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню.
 */
@StringDef({
    MenuNavigate.PROFILE,
    MenuNavigate.BALANCE,
    MenuNavigate.MESSAGES,
    MenuNavigate.HISTORY,
    MenuNavigate.OPERATOR,
    MenuNavigate.VEHICLES
})
@Retention(RetentionPolicy.SOURCE)
public @interface MenuNavigate {

  // Переход к профилю.
  String PROFILE = "Menu.to.Profile";

  // Переход к балансу.
  String BALANCE = "Menu.to.Balance";

  // Переход к решению недостатка средств.
  String MESSAGES = "Menu.to.Messages";

  // Переход к решению отсутствия ТС.
  String HISTORY = "Menu.to.History";

  // Переход к решению отсутствия свободных ТС.
  String OPERATOR = "Menu.to.Operator";

  // Переход к решению отсутствия свободных ТС.
  String VEHICLES = "Menu.to.Vehicles";
}
