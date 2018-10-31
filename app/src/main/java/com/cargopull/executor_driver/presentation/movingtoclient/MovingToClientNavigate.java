package com.cargopull.executor_driver.presentation.movingtoclient;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    MovingToClientNavigate.CALL_TO_CLIENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface MovingToClientNavigate {

  // Переход к карте для выхода на линию.
  String CALL_TO_CLIENT = "MovingToClient.to.CallToClient";
}
