package com.cargopull.executor_driver.presentation.waitingforclient;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    WaitingForClientNavigate.CALL_TO_CLIENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface WaitingForClientNavigate {

  // Переход к карте для выхода на линию.
  String CALL_TO_CLIENT = "WaitingForClient.to.CallToClient";
}
