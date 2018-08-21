package com.cargopull.executor_driver;

import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Тестовое правило, которое задает последовательные шедуллеры для юзкейсов.
 */

public class UseCaseThreadTestRule implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        RxJavaPlugins.reset();
        RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
        base.evaluate();
        RxJavaPlugins.reset();
      }
    };
  }

}
