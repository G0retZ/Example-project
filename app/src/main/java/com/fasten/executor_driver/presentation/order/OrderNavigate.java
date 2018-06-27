package com.fasten.executor_driver.presentation.order;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна звонка клиенту.
 */
@StringDef({
    OrderNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
@interface OrderNavigate {

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "Order.to.ServerDataError";
}
