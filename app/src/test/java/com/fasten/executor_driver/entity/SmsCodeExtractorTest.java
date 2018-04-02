package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SmsCodeExtractorTest {

  @Test
  public void messageIsNull() {
    assertEquals(new SmsCodeExtractor().extractCode(null), null);
  }

  @Test
  public void messageIsEmpty() {
    assertEquals(new SmsCodeExtractor().extractCode(""), "");
  }

  @Test
  public void messageIsShort() {
    assertEquals(new SmsCodeExtractor().extractCode("sd09sd"), "sd09sd");
  }

  @Test
  public void messageIsLong() {
    assertEquals(new SmsCodeExtractor().extractCode("sd09sdd09s009s0ss0"), "sd09sdd09s009s0ss0");
  }

  @Test
  public void messageHasNotOnlyNumbers() {
    assertEquals(new SmsCodeExtractor().extractCode("d09sdd09s0"), "d09sdd09s0");
  }

  @Test
  public void messageCorrect() {
    assertEquals(new SmsCodeExtractor().extractCode("70902832921"), "70902832921");
  }
}