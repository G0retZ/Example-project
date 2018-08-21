package com.cargopull.executor_driver;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Тестовое правило, которое задает последовательные шедуллеры для вьюмоделей.
 */

public class ViewModelThreadTestRule implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        RxAndroidPlugins.reset();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        base.evaluate();
        RxAndroidPlugins.reset();
      }
    };
  }

}
