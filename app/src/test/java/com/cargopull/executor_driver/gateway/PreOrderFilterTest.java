package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
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
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() throws Exception {
    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен выдать ошибку потери актуальности предложения, если сообщение с заголовком
   * PreliminaryExpired.
   */
  @Test(expected = OrderOfferExpiredException.class)
  public void errorForHeaderWithPreliminaryExpiredTrue() throws Exception {
    // Дано:
    when(stompMessage.findHeader("PreliminaryExpired")).thenReturn("true");

    // Действие и Результат:
    filter.test(stompMessage);
  }

  /**
   * Должен выдать ошибку потери актуальности предложения, если сообщение с заголовком
   * PreliminaryExpired и пайлоадом.
   */
  @Test(expected = OrderOfferExpiredException.class)
  public void errorForHeaderWithPreliminaryExpiredTrueAndPayload() throws Exception {
    // Дано:
    when(stompMessage.findHeader("PreliminaryExpired")).thenReturn("true");
    when(stompMessage.getPayload()).thenReturn("\n");

    // Действие и Результат:
    filter.test(stompMessage);
  }

  /**
   * Должен выдать ошибку отмены заказа клиентом, если сообщение с заголовком PreliminaryCancelled.
   */
  @Test(expected = OrderCancelledException.class)
  public void errorForHeaderWithPreliminaryCancelledTrue() throws Exception {
    // Дано:
    when(stompMessage.findHeader("PreliminaryCancelled")).thenReturn("true");

    // Действие и Результат:
    filter.test(stompMessage);
  }

  /**
   * Должен выдать ошибку отмены заказа клиентом, если сообщение с заголовком PreliminaryCancelled и
   * пайлоадом.
   */
  @Test(expected = OrderCancelledException.class)
  public void errorForHeaderWithPreliminaryCancelledTrueAndPayload() throws Exception {
    // Дано:
    when(stompMessage.findHeader("PreliminaryCancelled")).thenReturn("true");
    when(stompMessage.getPayload()).thenReturn("\n");

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