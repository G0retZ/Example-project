package com.cargopull.executor_driver.entity;

import org.junit.Test;

public class PhoneNumberValidatorTest {

  @Test(expected = ValidationException.class)
  public void phoneNumberIsNull() throws Exception {
    new PhoneNumberValidator().validate(null);
  }

  @Test(expected = ValidationException.class)
  public void phoneNumberIsEmpty() throws Exception {
    new PhoneNumberValidator().validate("");
  }

  @Test(expected = ValidationException.class)
  public void phoneNumberIsShort() throws Exception {
    new PhoneNumberValidator().validate("sd09sd");
  }

  @Test(expected = ValidationException.class)
  public void phoneNumberIsLong() throws Exception {
    new PhoneNumberValidator().validate("sd09sdd09s009s0ss0");
  }

  @Test(expected = ValidationException.class)
  public void phoneNumberHasNotOnlyNumbers() throws Exception {
    new PhoneNumberValidator().validate("d09sdd09s0");
  }

  @Test(expected = ValidationException.class)
  public void phoneNumberStartsNotWith7() throws Exception {
    new PhoneNumberValidator().validate("80902832921");
  }

  @Test
  public void phoneNumberCorrect() throws Exception {
    new PhoneNumberValidator().validate("70902832921");
  }
}
