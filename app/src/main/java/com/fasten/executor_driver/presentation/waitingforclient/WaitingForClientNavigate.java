package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    WaitingForClientNavigate.CALL_TO_CLIENT,
    WaitingForClientNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface WaitingForClientNavigate {

  // Переход к карте для выхода на линию.
  String CALL_TO_CLIENT = "WaitingForClient.to.CallToClient";

  // Переход к ошибке соединения.
  String NO_CONNECTION = "WaitingForClient.to.NoConnection";
}
