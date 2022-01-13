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
public class OrderFilterTest {

  private OrderFilter filter;
  @Mock
  private StompFrame stompFrame;

  @Before
  public void setUp() {
    filter = new OrderFilter();
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
   * Должен отказать, если сообщение с заголовком Status с неверным занчением.
   */
  @Test
  public void filterForHeaderWithWrongValue() {
    // Given:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("Status", ""));

    // Action и Effect:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = DRIVER_ORDER_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithDriverOrderConfirmation() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "DRIVER_ORDER_CONFIRMATION"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = CLIENT_ORDER_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithClientOrderConfirmation() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "CLIENT_ORDER_CONFIRMATION"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = MOVING_TO_CLIENT.
   */
  @Test
  public void allowForHeaderWithMovingToClient() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "MOVING_TO_CLIENT"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = WAITING_FOR_CLIENT.
   */
  @Test
  public void allowForHeaderWithWaitingForClient() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "WAITING_FOR_CLIENT"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = ORDER_FULFILLMENT.
   */
  @Test
  public void allowForHeaderWithOrderFulfillment() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "ORDER_FULFILLMENT"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = PAYMENT_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithPaymentConfirmation() {
    // Given:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "PAYMENT_CONFIRMATION"));

    // Action и Effect:
    assertTrue(filter.test(stompFrame));
  }
}