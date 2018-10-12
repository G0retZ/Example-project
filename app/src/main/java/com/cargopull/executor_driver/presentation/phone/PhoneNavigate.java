package com.cargopull.executor_driver.presentation.phone;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из ввода логина.
 */
@SuppressWarnings("SpellCheckingInspection")
@StringDef({
    PhoneNavigate.PASSWORD
})
@Retention(RetentionPolicy.SOURCE)
public @interface PhoneNavigate {

  // Переход к вводу пароля.
  String PASSWORD = "Phone.to.Password";
}
