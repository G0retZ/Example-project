package com.cargopull.executor_driver.entity;

import org.junit.Test;

public class LoginValidatorTest {

  @Test(expected = ValidationException.class)
  public void loginIsNull() throws Exception {
    new LoginValidator().validate(null);
  }

  @Test(expected = ValidationException.class)
  public void loginIsEmpty() throws Exception {
    new LoginValidator().validate("");
  }

  @Test(expected = ValidationException.class)
  public void loginIsShort() throws Exception {
    new LoginValidator().validate("sd09sd");
  }

  @Test(expected = ValidationException.class)
  public void loginIsLong() throws Exception {
    new LoginValidator().validate("sd09sdd09s009s0ss0");
  }

  @Test(expected = ValidationException.class)
  public void loginHasNotOnlyNumbers() throws Exception {
    new LoginValidator().validate("d09sdd09s0");
  }

  @Test(expected = ValidationException.class)
  public void loginStartsNotWith7() throws Exception {
    new PhoneNumberValidator().validate("80902832921");
  }

  @Test
  public void loginCorrect() throws Exception {
    new LoginValidator().validate("70902832921");
  }
}
