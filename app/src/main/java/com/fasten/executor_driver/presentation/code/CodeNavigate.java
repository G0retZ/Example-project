package com.fasten.executor_driver.presentation.code;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна ввода кода-пароля.
 */
@StringDef({
    CodeNavigate.MAP
})
@Retention(RetentionPolicy.SOURCE)
public @interface CodeNavigate {

  // Переход к карте для выхода на линию.
  String MAP = "ChooseVehicle.to.Map";
}
