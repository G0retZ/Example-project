package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateFilterTest {

  private ExecutorStateFilter filter;
  @Mock
  private StompFrame stompFrame;

  @Before
  public void setUp() {
    filter = new ExecutorStateFilter();
  }

  /**
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() {
    // Action и Effect:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status.
   */
  @Test
  public void allowForHeaderWithCorrectValue() {
    // Given:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("Status", ""));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }
}