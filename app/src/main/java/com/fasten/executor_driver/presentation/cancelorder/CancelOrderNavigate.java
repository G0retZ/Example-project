package com.fasten.executor_driver.presentation.cancelorder;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна выбора причины отказа.
 */
@StringDef({
    CancelOrderNavigate.ORDER_CANCELED,
    CancelOrderNavigate.NO_CONNECTION,
    CancelOrderNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
public @interface CancelOrderNavigate {

  // Переход к "заказ отменен".
  String ORDER_CANCELED = "CancelOrder.to.OrderCanceled";

  // Переход к ошибке соединения.
  String NO_CONNECTION = "CancelOrder.to.NoConnection";

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "CancelOrder.to.ServerDataError";
}
