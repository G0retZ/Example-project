package com.cargopull.executor_driver.backend.web.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApiOrderDecisionTest {

  @Test
  public void testConstructorWithFalse() {
    // Given:
    ApiOrderDecision apiOrderDecision = new ApiOrderDecision(1, false);

      // Effect:
    assertEquals(apiOrderDecision.getId(), 1);
    assertFalse(apiOrderDecision.isApproved());
  }

  @Test
  public void testConstructorWithTrue() {
      // Given:
    ApiOrderDecision apiOrderDecision = new ApiOrderDecision(5, true);

      // Effect:
    assertEquals(apiOrderDecision.getId(), 5);
    assertTrue(apiOrderDecision.isApproved());
  }
}