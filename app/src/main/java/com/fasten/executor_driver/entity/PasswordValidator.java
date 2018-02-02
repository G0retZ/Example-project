package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;
import javax.inject.Inject;

/**
 * Валидатор пароля
 */
public class PasswordValidator implements Validator<String> {

  @Inject
  PasswordValidator() {
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean validate(@Nullable String password) {
    if (password == null) {
      return false;
    }
    if (password.length() != 4) {
      return false;
    }
    return password.matches("\\d*");
  }
}
