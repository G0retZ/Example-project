package com.fasten.executor_driver.presentation.ordertime;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна звонка клиенту.
 */
@StringDef({
    OrderTimeNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
@interface OrderTimeNavigate {

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "OrderTime.to.ServerDataError";
}
