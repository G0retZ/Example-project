package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.ExecutorState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateSharerTest {

  private ExecutorStateSharer executorStateSharer;

  @Before
  public void setUp() throws Exception {
    executorStateSharer = new ExecutorStateSharer();
  }

  /**
   * Должен получить значение без изменений.
   *
   * @throws Exception error
   */
  @Test
  public void valueUnchangedForRead() throws Exception {
    // Дано:
    executorStateSharer.share(ExecutorState.APPROACHING_LOADING_POINT);

    // Действие и Результат:
    executorStateSharer.get().test().assertValue(ExecutorState.APPROACHING_LOADING_POINT);
  }
}