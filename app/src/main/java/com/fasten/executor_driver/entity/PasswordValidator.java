package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;
import javax.inject.Inject;

/**
 * Валидатор пароля.
 */
public class PasswordValidator implements Validator<String> {

  @Inject
  PasswordValidator() {
  }

  @Override
  public void validate(@Nullable String password) throws Exception {
    if (password == null || password.length() != 4 || !password.matches("\\d*")) {
      throw new ValidationException();
    }
  }
}
