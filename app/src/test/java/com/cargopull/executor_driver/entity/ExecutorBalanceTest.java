package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExecutorBalanceTest {

  @Test
  public void testConstructor() {
    ExecutorBalance cancelOrderReason = new ExecutorBalance(1, 2, 3);
    assertEquals(cancelOrderReason.getMainAccount(), 1);
    assertEquals(cancelOrderReason.getBonusAccount(), 2);
    assertEquals(cancelOrderReason.getCashlessAccount(), 3);
  }
}