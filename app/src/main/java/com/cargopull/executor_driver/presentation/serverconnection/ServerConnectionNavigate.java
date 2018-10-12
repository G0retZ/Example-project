package com.cargopull.executor_driver.presentation.serverconnection;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при подключении к серверу.
 */
@StringDef({
    ServerConnectionNavigate.AUTHORIZE,
    ServerConnectionNavigate.VERSION_DEPRECATED
})
@Retention(RetentionPolicy.SOURCE)
public @interface ServerConnectionNavigate {

  // Переход к авторизации.
  String AUTHORIZE = "ServerConnection.to.Authorization";

  // Переход к ошибке запрещенной версии.
  String VERSION_DEPRECATED = "ServerConnection.to.VersionDeprecated";
}
