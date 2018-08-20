package com.cargopull.executor_driver.backend.web.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApiOrderDecisionTest {

  @Test
  public void testConstructorWithFalse() {
    // Дано:
    ApiOrderDecision apiOrderDecision = new ApiOrderDecision(1, false);

    // Результат:
    assertEquals(apiOrderDecision.getId(), 1);
    assertFalse(apiOrderDecision.isApproved());
  }

  @Test
  public void testConstructorWithTrue() {
    // Дано:
    ApiOrderDecision apiOrderDecision = new ApiOrderDecision(5, true);

    // Результат:
    assertEquals(apiOrderDecision.getId(), 5);
    assertTrue(apiOrderDecision.isApproved());
  }
}