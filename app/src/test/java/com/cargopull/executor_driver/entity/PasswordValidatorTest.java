package com.cargopull.executor_driver.entity;

import org.junit.Test;

public class PasswordValidatorTest {

  @Test(expected = ValidationException.class)
  public void passwordIsNull() throws Exception {
    new PasswordValidator().validate(null);
  }

  @Test(expected = ValidationException.class)
  public void passwordIsEmpty() throws Exception {
    new PasswordValidator().validate("");
  }

  @Test(expected = ValidationException.class)
  public void passwordIsShort() throws Exception {
    new PasswordValidator().validate("sow");
  }

  @Test(expected = ValidationException.class)
  public void passwordIsLong() throws Exception {
    new PasswordValidator().validate("sd09sdd09s009s0ss0");
  }

  @Test(expected = ValidationException.class)
  public void passwordHasNotOnlyNumbers() throws Exception {
    new PasswordValidator().validate("d09sdd09s0");
  }

  @Test
  public void passwordCorrect() throws Exception {
    new PasswordValidator().validate("0903");
  }
}
