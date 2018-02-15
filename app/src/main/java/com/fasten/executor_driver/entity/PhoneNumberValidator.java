package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;
import javax.inject.Inject;

/**
 * Валидатор номера телефона
 */
public class PhoneNumberValidator implements Validator<String> {

  @Inject
  PhoneNumberValidator() {
  }

  @Override
  public void validate(@Nullable String phoneNumber) throws Exception {
    if (phoneNumber == null || phoneNumber.length() != 11 || !phoneNumber.matches("7\\d*")) {
      throw new ValidationException();
    }
  }
}
