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
public class CancelOrderReasonsFilterTest {

  private CancelOrderReasonsFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new CancelOrderReasonsFilter();
  }

  /**
   * Должен отказать, если статус не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() throws Exception {
    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком CancelReason.
   */
  @Test
  public void allowForHeaderWithCorrectValue() throws Exception {
    // Дано:
    when(stompMessage.findHeader("CancelReason")).thenReturn("");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}