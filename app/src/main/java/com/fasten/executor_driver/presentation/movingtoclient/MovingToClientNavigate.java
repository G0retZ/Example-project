package com.fasten.executor_driver.presentation.movingtoclient;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    MovingToClientNavigate.CALL_TO_CLIENT,
    MovingToClientNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface MovingToClientNavigate {

  // Переход к карте для выхода на линию.
  String CALL_TO_CLIENT = "MovingToClient.to.CallToClient";

  // Переход к ошибке соединения.
  String NO_CONNECTION = "MovingToClient.to.NoConnection";
}
