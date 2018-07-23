package com.cargopull.executor_driver.entity;

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

  @Test
  public void testToString() {
    CancelOrderReason cancelOrderReason = new CancelOrderReason(7, "me", "used");
    assertEquals(cancelOrderReason.toString(),
        "CancelOrderReason{id=7, name='me', unusedName='used'}");
  }
}