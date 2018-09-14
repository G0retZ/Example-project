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
public class OrderCostDetailsFilterTest {

  private OrderCostDetailsFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new OrderCostDetailsFilter();
  }

  /**
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void filterIfExecutorStateIncorrect() {
    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен отказать, если сообщение с заголовком Status с неверным занчением.
   */
  @Test
  public void filterForHeaderWithWrongValue() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("");

    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status с верным занчением.
   */
  @Test
  public void allowForHeaderWithCorrectValue() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("PAYMENT_CONFIRMATION");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}