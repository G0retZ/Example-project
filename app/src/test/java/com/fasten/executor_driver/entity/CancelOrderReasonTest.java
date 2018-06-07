package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CancelOrderReasonTest {

  @Test
  public void testConstructor() {
    CancelOrderReason cancelOrderReason = new CancelOrderReason(3, "name", "unused");
    assertEquals(cancelOrderReason.getId(), 3);
    assertEquals(cancelOrderReason.getName(), "name");
    assertEquals(cancelOrderReason.getUnusedName(), "unused");
  }
}