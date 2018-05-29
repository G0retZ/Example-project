package com.fasten.executor_driver.presentation.oderfulfillmentmenu;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из меню действий выполнения заказа.
 */
@StringDef({
    OrderFulfillmentMenuNavigate.ORDER_ROUTE,
    OrderFulfillmentMenuNavigate.PAUSE,
    OrderFulfillmentMenuNavigate.COST_DETAILED,
    OrderFulfillmentMenuNavigate.ORDER_INFORMATION,
    OrderFulfillmentMenuNavigate.CALL_TO_CLIENT,
    OrderFulfillmentMenuNavigate.ADD_SERVICE,
    OrderFulfillmentMenuNavigate.CANCEL_ORDER
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderFulfillmentMenuNavigate {

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
  String CANCEL_ORDER = "OrderFulfillmentMenu.to.CancelOrder";
}
