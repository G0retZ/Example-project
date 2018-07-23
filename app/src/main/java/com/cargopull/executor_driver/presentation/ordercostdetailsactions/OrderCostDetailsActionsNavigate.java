package com.cargopull.executor_driver.presentation.ordercostdetailsactions;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий движения к клиенту.
 */
@StringDef({
    OrderCostDetailsActionsNavigate.ORDER_ROUTE,
    OrderCostDetailsActionsNavigate.ORDER_INFORMATION,
    OrderCostDetailsActionsNavigate.REPORT_A_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderCostDetailsActionsNavigate {

  // Переход к маршруту заказа.
  String ORDER_ROUTE = "OrderCostDetailsActions.to.OrderRoute";

  // Переход к деталям заказа.
  String ORDER_INFORMATION = "OrderCostDetailsActions.to.OrderInformation";

  // Переход к решению проблемы.
  String REPORT_A_PROBLEM = "OrderCostDetailsActions.to.CancelOrder";
}
