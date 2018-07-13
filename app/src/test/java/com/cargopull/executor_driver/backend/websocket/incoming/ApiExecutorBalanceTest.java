package com.cargopull.executor_driver.backend.websocket.incoming;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApiExecutorBalanceTest {

  @Test
  public void testConstructor() {
    ApiExecutorBalance cancelOrderReason = new ApiExecutorBalance(4, 5, 6);
    assertEquals(cancelOrderReason.getMainAccount(), 4);
    assertEquals(cancelOrderReason.getBonusAccount(), 5);
    assertEquals(cancelOrderReason.getNonCashAccount(), 6);
  }

  @Test
  public void testConstructorWithNull() {
    ApiExecutorBalance cancelOrderReason = new ApiExecutorBalance(0, 0, 0);
    assertEquals(cancelOrderReason.getMainAccount(), 0);
    assertEquals(cancelOrderReason.getBonusAccount(), 0);
    assertEquals(cancelOrderReason.getNonCashAccount(), 0);
  }
}