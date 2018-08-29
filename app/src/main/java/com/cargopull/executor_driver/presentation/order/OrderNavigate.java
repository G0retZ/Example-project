package com.cargopull.executor_driver.presentation.order;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации заказа.
 */
@StringDef({
    OrderNavigate.ORDER_EXPIRED
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderNavigate {

  // Переход к просрочке заказа.
  String ORDER_EXPIRED = "Order.to.OrderExpired";
}
