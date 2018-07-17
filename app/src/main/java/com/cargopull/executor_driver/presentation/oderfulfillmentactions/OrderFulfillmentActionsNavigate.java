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

  // Переход к маршруту заказа.
  String ORDER_ROUTE = "OrderFulfillmentActions.to.OrderRoute";

  // Переход к паузе заказа.
  String PAUSE = "OrderFulfillmentActions.to.Pause";

  // Переход к детальному расчету.
  String COST_DETAILED = "OrderFulfillmentActions.to.CostDetailed";

  // Переход к деталям заказа.
  String ORDER_INFORMATION = "OrderFulfillmentActions.to.OrderInformation";

  // Переход к звонку клиенту.
  String CALL_TO_CLIENT = "OrderFulfillmentActions.to.CallToClient";

  // Переход к добавлению услуги.
  String ADD_SERVICE = "OrderFulfillmentActions.to.AddService";

  // Переход к решению проблемы.
  String REPORT_A_PROBLEM = "OrderFulfillmentActions.to.CancelOrder";
}
