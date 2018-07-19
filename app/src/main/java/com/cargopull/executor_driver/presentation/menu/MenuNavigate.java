package com.cargopull.executor_driver.presentation.menu;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню.
 */
@StringDef({
    MenuNavigate.BALANCE
})
@Retention(RetentionPolicy.SOURCE)
public @interface MenuNavigate {

  // Переход к балансу.
  String BALANCE = "Menu.to.Balance";
}
