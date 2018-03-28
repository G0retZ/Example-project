package com.fasten.executor_driver.presentation.splahscreen;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при старте приложения.
 */
@StringDef({
    SplashScreenNavigate.NO_NETWORK,
    SplashScreenNavigate.AUTHORIZE,
    SplashScreenNavigate.MAP_SHIFT_CLOSED,
    SplashScreenNavigate.MAP_SHIFT_OPENED,
    SplashScreenNavigate.MAP_ONLINE
})
@Retention(RetentionPolicy.SOURCE)
public @interface SplashScreenNavigate {

  // Переход к решению проблем сети.
  String NO_NETWORK = "to.NoNetwork";
  // Переход к авторизации.
  String AUTHORIZE = "to.Authorization";
  // Переход к карте.
  String MAP_SHIFT_CLOSED = "to.Map.Shift.Closed";
  // Переход к карте.
  String MAP_SHIFT_OPENED = "to.Map.Shift.Opened";
  // Переход к получению заказов.
  String MAP_ONLINE = "to.Map.Online";
}
