package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class CancelledOrderFilterTest {

  private CancelledOrderFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new CancelledOrderFilter();
  }

  /**
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() {
    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен отказать, если сообщение с заголовком PreliminaryCancelled = true.
   */
  @Test
  public void denyForHeaderWithWrongValue() {
    // Дано:
    when(stompMessage.findHeader("PreliminaryCancelled")).thenReturn("true");

    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком PreliminaryCancelled.
   */
  @Test
  public void allowForHeaderWithOtherValue() {
    // Дано:
    when(stompMessage.findHeader("PreliminaryCancelled")).thenReturn("");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}