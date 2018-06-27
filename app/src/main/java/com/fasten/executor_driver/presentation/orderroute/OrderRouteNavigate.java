package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна звонка клиенту.
 */
@StringDef({
    OrderRouteNavigate.NO_CONNECTION,
    OrderRouteNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
@interface OrderRouteNavigate {

  // Переход к ошибке соединения.
  String NO_CONNECTION = "OrderRoute.to.NoConnection";
  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "OrderRoute.to.ServerDataError";
}
