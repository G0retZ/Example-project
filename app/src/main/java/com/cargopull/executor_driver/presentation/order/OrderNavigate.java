package com.cargopull.executor_driver.presentation.order;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации с карточки заказа.
 */
@StringDef({
    OrderNavigate.CLOSE
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderNavigate {

  // Переход к закрыти карточки заказа.
  String CLOSE = "Order.to.Close";
}
