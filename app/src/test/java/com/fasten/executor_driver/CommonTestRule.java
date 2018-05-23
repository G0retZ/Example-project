package com.fasten.executor_driver;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Тестовое правило, которое имплементирует {@link TestRule#apply(Statement, Description)} метод
 * to evaluate the base statement.
 */

public class CommonTestRule implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        base.evaluate();
      }
    };
  }

}
