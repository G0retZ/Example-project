package com.cargopull.executor_driver.presentation.reportproblem;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна выбора причины отказа.
 */
@StringDef({
    ReportProblemNavigate.ORDER_CANCELED
})
@Retention(RetentionPolicy.SOURCE)
public @interface ReportProblemNavigate {

  // Переход к "заказ отменен".
  String ORDER_CANCELED = "CancelOrder.to.OrderCanceled";
}
