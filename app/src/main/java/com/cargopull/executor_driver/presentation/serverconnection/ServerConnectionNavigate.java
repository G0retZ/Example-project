package com.cargopull.executor_driver.presentation.serverconnection;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при подключении к серверу.
 */
@StringDef({
    ServerConnectionNavigate.AUTHORIZE
})
@Retention(RetentionPolicy.SOURCE)
public @interface ServerConnectionNavigate {

  // Переход к авторизации.
  String AUTHORIZE = "ServerConnection.to.Authorization";
}
