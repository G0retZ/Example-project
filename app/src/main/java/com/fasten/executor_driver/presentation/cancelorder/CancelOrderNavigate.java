package com.fasten.executor_driver.presentation.cancelorder;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна выбора причины отказа.
 */
@StringDef({
    CancelOrderNavigate.ORDER_CANCELED,
    CancelOrderNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface CancelOrderNavigate {

  // Переход к "заказ отменен".
  String ORDER_CANCELED = "CancelOrder.to.OrderCanceled";
  String NO_CONNECTION = "CancelOrder.to.NoConnection";
}
