package com.fasten.executor_driver.entity;

import javax.inject.Inject;

/**
 * Валидатор логина
 */
public class LoginValidator extends PhoneNumberValidator {

  @Inject
  LoginValidator() {
    super();
  }
}
