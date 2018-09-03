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
public class UpdateMessageFilterTest {


  private UpdateMessageFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new UpdateMessageFilter();
  }

  /**
   * Должен отказать, если статус не соответствует фильтруемому.
   */
  @Test
  public void filterIfExecutorStateIncorrect() {
    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен отказать, если сообщение с заголовком message с неверным занчением.
   */
  @Test
  public void filterForHeaderWithWrongValue() {
    // Дано:
    when(stompMessage.findHeader("message")).thenReturn("");

    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком message с верным занчением.
   */
  @Test
  public void allowForHeaderWithCorrectValue() {
    // Дано:
    when(stompMessage.findHeader("message")).thenReturn("UpdateVersion");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}