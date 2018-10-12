package com.cargopull.executor_driver.presentation.movingtoclientactions;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий движения к клиенту.
 */
@StringDef({
    MovingToClientActionsNavigate.ORDER_ROUTE,
    MovingToClientActionsNavigate.ORDER_INFORMATION,
    MovingToClientActionsNavigate.REPORT_A_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface MovingToClientActionsNavigate {

  // Переход к маршруту заказа.
  String ORDER_ROUTE = "MovingToClientActions.to.OrderRoute";

  // Переход к деталям заказа.
  String ORDER_INFORMATION = "MovingToClientActions.to.OrderInformation";

  // Переход к решению проблемы.
  String REPORT_A_PROBLEM = "MovingToClientActions.to.CancelOrder";
}
