package com.fasten.executor_driver.entity;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordValidatorTest {

  @Test
  public void passwordIsNull() throws Exception {
    assertFalse(new PasswordValidator().validate(null));
  }

  @Test
  public void passwordIsEmpty() throws Exception {
    assertFalse(new PasswordValidator().validate(""));
  }

  @Test
  public void passwordIsShort() throws Exception {
    assertFalse(new PasswordValidator().validate("sow"));
  }

  @Test
  public void passwordIsLong() throws Exception {
    assertFalse(new PasswordValidator().validate("sd09sdd09s009s0ss0"));
  }

  @Test
  public void passwordHasNotOnlyNumbers() throws Exception {
    assertFalse(new PasswordValidator().validate("d09sdd09s0"));
  }

  @Test
  public void passwordCorrect() throws Exception {
    assertTrue(new PasswordValidator().validate("0903"));
  }
}
