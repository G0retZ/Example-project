package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.ExecutorState;
import javax.inject.Inject;

public class ExecutorStateSharer extends MemoryDataSharer<ExecutorState> {

  @Inject
  public ExecutorStateSharer() {
  }
}
