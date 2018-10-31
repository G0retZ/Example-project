package com.cargopull.executor_driver.presentation.oderfulfillmentactions;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий выполнения заказа.
 */
@StringDef({
    OrderFulfillmentActionsNavigate.ORDER_ROUTE,
    OrderFulfillmentActionsNavigate.ORDER_INFORMATION,
    OrderFulfillmentActionsNavigate.CALL_TO_CLIENT,
    OrderFulfillmentActionsNavigate.REPORT_A_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderFulfillmentActionsNavigate {

  // Переход к маршруту заказа.
  String ORDER_ROUTE = "OrderFulfillmentActions.to.OrderRoute";

  // Переход к деталям заказа.
  String ORDER_INFORMATION = "OrderFulfillmentActions.to.OrderInformation";

  // Переход к звонку клиенту.
  String CALL_TO_CLIENT = "OrderFulfillmentActions.to.CallToClient";

  // Переход к решению проблемы.
  String REPORT_A_PROBLEM = "OrderFulfillmentActions.to.CancelOrder";
}
