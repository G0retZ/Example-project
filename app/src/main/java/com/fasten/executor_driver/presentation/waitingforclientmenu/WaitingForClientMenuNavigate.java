package com.fasten.executor_driver.presentation.waitingforclientmenu;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий выполнения заказа.
 */
@StringDef({
    WaitingForClientMenuNavigate.ORDER_ROUTE,
    WaitingForClientMenuNavigate.REPORT_A_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface WaitingForClientMenuNavigate {

  // Переход к профилю.
  String ORDER_ROUTE = "WaitingForClientMenu.to.OrderRoute";

  // Переход к решению отсутствия свободных ТС.
  String REPORT_A_PROBLEM = "WaitingForClientMenu.to.CancelOrder";
}
