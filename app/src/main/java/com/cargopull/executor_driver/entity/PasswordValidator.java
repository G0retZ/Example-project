package com.cargopull.executor_driver.entity;

import androidx.annotation.Nullable;
import javax.inject.Inject;

/**
 * Валидатор пароля.
 */
public class PasswordValidator implements Validator<String> {

  @Inject
  public PasswordValidator() {
  }

  @Override
  public void validate(@Nullable String password) throws Exception {
    if (password == null || password.length() != 4 || !password.matches("\\d*")) {
      throw new ValidationException();
    }
  }
}
