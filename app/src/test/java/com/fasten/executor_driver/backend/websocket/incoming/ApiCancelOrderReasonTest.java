package com.fasten.executor_driver.backend.websocket.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ApiCancelOrderReasonTest {

  @Test
  public void testConstructor() {
    ApiCancelOrderReason cancelOrderReason = new ApiCancelOrderReason(4, "name");
    assertEquals(cancelOrderReason.getId(), 4);
    assertEquals(cancelOrderReason.getDescription(), "name");
  }

  @Test
  public void testConstructorWithNull() {
    ApiCancelOrderReason cancelOrderReason = new ApiCancelOrderReason(0, null);
    assertEquals(cancelOrderReason.getId(), 0);
    assertNull(cancelOrderReason.getDescription());
  }
}