package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    // Действие и Результат:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен отказать, если сообщение с заголовком PreliminaryChanged.
   */
  @Test
  public void denyForHeaderWithWrongValue() {
    // Дано:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("PreliminaryChanged", ""));

    // Действие и Результат:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком PreliminaryChanged = true.
   */
  @Test
  public void allowForHeaderWithOtherValue() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("PreliminaryChanged", "true"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }
}