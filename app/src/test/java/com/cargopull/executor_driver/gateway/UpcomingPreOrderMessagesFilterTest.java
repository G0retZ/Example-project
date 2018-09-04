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
public class UpcomingPreOrderMessagesFilterTest {

  private UpcomingPreOrderMessagesFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new UpcomingPreOrderMessagesFilter();
  }

  /**
   * Должен отказать, если нет верного заголовка.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() {
    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком PreliminaryReminder.
   */
  @Test
  public void allowForHeaderWithCorrectValue() {
    // Дано:
    when(stompMessage.findHeader("PreliminaryReminder")).thenReturn("");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}