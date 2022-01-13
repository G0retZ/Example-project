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
public class ChangedOrderFilterTest {

  private ChangedOrderFilter filter;
  @Mock
  private StompFrame stompFrame;

  @Before
  public void setUp() {
    filter = new ChangedOrderFilter();
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
   * Должен отказать, если сообщение с заголовком PreliminaryChanged.
   */
  @Test
  public void denyForHeaderWithWrongValue() {
    // Given:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("PreliminaryChanged", ""));

    // Action и Effect:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком PreliminaryChanged = true.
   */
  @Test
  public void allowForHeaderWithOtherValue() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("PreliminaryChanged", "true"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }
}