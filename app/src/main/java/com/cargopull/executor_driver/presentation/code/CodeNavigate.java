package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    CodeNavigate.ENTER_APP
})
@Retention(RetentionPolicy.SOURCE)
public @interface CodeNavigate {

  // Переход к карте для выхода на линию.
  String ENTER_APP = "Code.enter.application";
}
