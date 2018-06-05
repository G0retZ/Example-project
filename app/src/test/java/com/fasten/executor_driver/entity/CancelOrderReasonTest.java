package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CancelOrderReasonTest {

  @Test
  public void testConstructor() {
    CancelOrderReason cancelOrderReason = new CancelOrderReason("id", "name");
    assertEquals(cancelOrderReason.getId(), "id");
    assertEquals(cancelOrderReason.getName(), "name");
  }
}