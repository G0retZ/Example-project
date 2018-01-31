package com.fasten.executor_driver.entity;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberValidatorTest {

  @Test
  public void phoneNumberIsNull() throws Exception {
    assertFalse(new PhoneNumberValidator().validate(null));
  }

  @Test
  public void phoneNumberIsEmpty() throws Exception {
    assertFalse(new PhoneNumberValidator().validate(""));
  }

  @Test
  public void phoneNumberIsShort() throws Exception {
    assertFalse(new PhoneNumberValidator().validate("sd09sd"));
  }

  @Test
  public void phoneNumberIsLong() throws Exception {
    assertFalse(new PhoneNumberValidator().validate("sd09sdd09s009s0ss0"));
  }

  @Test
  public void phoneNumberHasNotOnlyNumbers() throws Exception {
    assertFalse(new PhoneNumberValidator().validate("d09sdd09s0"));
  }

  @Test
  public void phoneNumberStartsNotWith7() throws Exception {
    assertFalse(new PhoneNumberValidator().validate("80902832921"));
  }

  @Test
  public void phoneNumberCorrect() throws Exception {
    assertTrue(new PhoneNumberValidator().validate("70902832921"));
  }
}
