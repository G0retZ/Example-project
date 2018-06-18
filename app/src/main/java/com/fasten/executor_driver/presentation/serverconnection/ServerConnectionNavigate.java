package com.fasten.executor_driver.presentation.serverconnection;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при подключении к серверу.
 */
@StringDef({
    ServerConnectionNavigate.NO_NETWORK,
    ServerConnectionNavigate.AUTHORIZE
})
@Retention(RetentionPolicy.SOURCE)
public @interface ServerConnectionNavigate {

  // Переход к решению проблем сети.
  String NO_NETWORK = "to.NoNetwork";
  // Переход к авторизации.
  String AUTHORIZE = "to.Authorization";
}
