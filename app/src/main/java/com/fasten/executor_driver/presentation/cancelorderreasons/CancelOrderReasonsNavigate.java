package com.fasten.executor_driver.presentation.cancelorderreasons;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при получении списка причин отказа от заказа.
 */
@StringDef({
    CancelOrderReasonsNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
public @interface CancelOrderReasonsNavigate {

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "CancelOrderReasons.to.ServerDataError";
}
