package com.cargopull.executor_driver.presentation.waitingforclientactions;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий ожидания клиента.
 */
@StringDef({
    WaitingForClientActionsNavigate.ORDER_ROUTE,
    WaitingForClientActionsNavigate.REPORT_A_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface WaitingForClientActionsNavigate {

  // Переход к маршруту заказа.
  String ORDER_ROUTE = "WaitingForClientActions.to.OrderRoute";

  // Переход к решению проблемы.
  String REPORT_A_PROBLEM = "WaitingForClientActions.to.CancelOrder";
}
