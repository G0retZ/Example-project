package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SmsCodeMapperTest {

  @Test
  public void messageIsEmpty() {
    assertEquals(new SmsCodeMapper().map(""), "");
  }

  @Test
  public void messageIsShort() {
    assertEquals(new SmsCodeMapper().map("sd09sd"), "sd09sd");
  }

  @Test
  public void messageIsLong() {
    assertEquals(new SmsCodeMapper().map("sd09sdd09s009s0ss0"), "sd09sdd09s009s0ss0");
  }

  @Test
  public void messageHasNotOnlyNumbers() {
    assertEquals(new SmsCodeMapper().map("d09sdd09s0"), "d09sdd09s0");
  }

  @Test
  public void messageCorrect() {
    assertEquals(new SmsCodeMapper().map("70902832921"), "70902832921");
  }
}