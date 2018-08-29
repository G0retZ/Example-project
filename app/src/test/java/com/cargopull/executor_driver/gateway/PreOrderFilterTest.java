package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.PreOrderExpiredException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderFilterTest {

  private PreOrderFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new PreOrderFilter();
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
   * Должен пропустить, если сообщение с заголовком PreliminaryExpired.
   */
  @Test(expected = PreOrderExpiredException.class)
  public void errorForHeaderWithPreliminaryExpiredTrue() throws Exception {
    // Дано:
    when(stompMessage.findHeader("PreliminaryExpired")).thenReturn("true");

    // Действие и Результат:
    filter.test(stompMessage);
  }

  /**
   * Должен пропустить, если сообщение с заголовком Preliminary.
   */
  @Test
  public void allowForHeaderWithCorrectValue() throws Exception {
    // Дано:
    when(stompMessage.findHeader("Preliminary")).thenReturn("");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}