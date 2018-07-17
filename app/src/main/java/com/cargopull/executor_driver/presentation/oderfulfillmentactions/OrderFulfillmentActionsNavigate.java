package com.cargopull.executor_driver.presentation.oderfulfillmentactions;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий выполнения заказа.
 */
@StringDef({
    OrderFulfillmentActionsNavigate.ORDER_ROUTE,
    OrderFulfillmentActionsNavigate.PAUSE,
    OrderFulfillmentActionsNavigate.COST_DETAILED,
    OrderFulfillmentActionsNavigate.ORDER_INFORMATION,
    OrderFulfillmentActionsNavigate.CALL_TO_CLIENT,
    OrderFulfillmentActionsNavigate.ADD_SERVICE,
    OrderFulfillmentActionsNavigate.REPORT_A_PROBLEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderFulfillmentActionsNavigate {

  // Переход к профилю.
  String ORDER_ROUTE = "OrderFulfillmentMenu.to.OrderRoute";

  // Переход к балансу.
  String PAUSE = "OrderFulfillmentMenu.to.Pause";

  // Переход к решению недостатка средств.
  String COST_DETAILED = "OrderFulfillmentMenu.to.CostDetailed";

  // Переход к решению отсутствия ТС.
  String ORDER_INFORMATION = "OrderFulfillmentMenu.to.OrderInformation";

  // Переход к решению отсутствия свободных ТС.
  String CALL_TO_CLIENT = "OrderFulfillmentMenu.to.CallToClient";

  // Переход к решению отсутствия свободных ТС.
  String ADD_SERVICE = "OrderFulfillmentMenu.to.AddService";

  // Переход к решению отсутствия свободных ТС.
  String REPORT_A_PROBLEM = "OrderFulfillmentMenu.to.CancelOrder";
}
