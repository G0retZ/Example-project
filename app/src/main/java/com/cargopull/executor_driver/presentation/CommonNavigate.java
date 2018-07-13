package com.cargopull.executor_driver.presentation;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при изменении статуса исполнителя.
 */
@StringDef({
    CommonNavigate.NO_CONNECTION,
    CommonNavigate.SERVER_DATA_ERROR,
    CommonNavigate.EXIT
})
@Retention(RetentionPolicy.SOURCE)
public @interface CommonNavigate {

  // Переход к ошибке соединения.
  String NO_CONNECTION = "CommonNavigate.to.NoConnection";

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "CommonNavigate.to.ServerDataError";

  // Выйти из приложения.
  String EXIT = "Menu.to.Exit";
}
