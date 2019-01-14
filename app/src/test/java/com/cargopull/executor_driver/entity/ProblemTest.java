package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProblemTest {

  @Test
  public void testConstructor() {
    Problem problem = new Problem(3, "name", "unused");
    assertEquals(problem.getId(), 3);
    assertEquals(problem.getName(), "name");
    assertEquals(problem.getUnusedName(), "unused");
  }

  @Test
  public void testToString() {
    Problem problem = new Problem(7, "me", "used");
    assertEquals(problem.toString(),
        "Problem{id=7, name='me', unusedName='used'}");
  }
}