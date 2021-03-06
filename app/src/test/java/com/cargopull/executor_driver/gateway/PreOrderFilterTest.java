package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderFilterTest {

  private PreOrderFilter filter;
  @Mock
  private StompFrame stompFrame;

  @Before
  public void setUp() {
    filter = new PreOrderFilter();
  }

  /**
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() throws Exception {
    // Action и Effect:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен выдать ошибку потери актуальности предложения, если сообщение с заголовком
   * PreliminaryExpired.
   */
  @Test(expected = OrderOfferExpiredException.class)
  public void errorForHeaderWithPreliminaryExpiredTrue() throws Exception {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("PreliminaryExpired", "true"));

    // Action и Effect:
    filter.test(stompFrame);
  }

  /**
   * Должен выдать ошибку потери актуальности предложения, если сообщение с заголовком
   * PreliminaryExpired и пайлоадом.
   */
  @Test(expected = OrderOfferExpiredException.class)
  public void errorForHeaderWithPreliminaryExpiredTrueAndPayload() throws Exception {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("PreliminaryExpired", "true"));
    when(stompFrame.getBody()).thenReturn("\n");

    // Action и Effect:
    filter.test(stompFrame);
  }

  /**
   * Должен выдать ошибку отмены заказа клиентом, если сообщение с заголовком PreliminaryCancelled.
   */
  @Test(expected = OrderCancelledException.class)
  public void errorForHeaderWithPreliminaryCancelledTrue() throws Exception {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("PreliminaryCancelled", "true"));

    // Action и Effect:
    filter.test(stompFrame);
  }

  /**
   * Должен выдать ошибку отмены заказа клиентом, если сообщение с заголовком PreliminaryCancelled и
   * пайлоадом.
   */
  @Test(expected = OrderCancelledException.class)
  public void errorForHeaderWithPreliminaryCancelledTrueAndPayload() throws Exception {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("PreliminaryCancelled", "true"));
    when(stompFrame.getBody()).thenReturn("\n");

    // Action и Effect:
    filter.test(stompFrame);
  }

  /**
   * Должен пропустить, если сообщение с заголовком Preliminary.
   */
  @Test
  public void allowForHeaderWithCorrectValue() throws Exception {
    // Given:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("Preliminary", ""));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }
}