package com.fasten.executor_driver.presentation.calltoclient;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    CallToClientNavigate.FINISHED
})
@Retention(RetentionPolicy.SOURCE)
public @interface CallToClientNavigate {

  // Переход к карте для выхода на линию.
  String FINISHED = "CallToClient.finish";
}
