package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна звонка клиенту.
 */
@StringDef({
    NextRoutePointNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
@interface NextRoutePointNavigate {

  // Переход к ошибке соединения.
  String NO_CONNECTION = "NextRoutePoint.to.NoConnection";
}
