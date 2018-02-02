package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LoginValidatorTest {

  @Test
  public void loginIsNull() throws Exception {
    assertFalse(new LoginValidator().validate(null));
  }

  @Test
  public void loginIsEmpty() throws Exception {
    assertFalse(new LoginValidator().validate(""));
  }

  @Test
  public void loginIsShort() throws Exception {
    assertFalse(new LoginValidator().validate("sd09sd"));
  }

  @Test
  public void loginIsLong() throws Exception {
    assertFalse(new LoginValidator().validate("sd09sdd09s009s0ss0"));
  }

  @Test
  public void loginHasNotOnlyNumbers() throws Exception {
    assertFalse(new LoginValidator().validate("d09sdd09s0"));
  }

  @Test
  public void loginStartsNotWith7() throws Exception {
    assertFalse(new PhoneNumberValidator().validate("80902832921"));
  }

  @Test
  public void loginCorrect() throws Exception {
    assertTrue(new LoginValidator().validate("70902832921"));
  }
}
