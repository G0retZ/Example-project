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
    // Действие и Результат:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен отказать, если сообщение с заголовком Status с неверным занчением.
   */
  @Test
  public void filterForHeaderWithWrongValue() {
    // Дано:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("Status", ""));

    // Действие и Результат:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = DRIVER_ORDER_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithDriverOrderConfirmation() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "DRIVER_ORDER_CONFIRMATION"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = CLIENT_ORDER_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithClientOrderConfirmation() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "CLIENT_ORDER_CONFIRMATION"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = MOVING_TO_CLIENT.
   */
  @Test
  public void allowForHeaderWithMovingToClient() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "MOVING_TO_CLIENT"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = WAITING_FOR_CLIENT.
   */
  @Test
  public void allowForHeaderWithWaitingForClient() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "WAITING_FOR_CLIENT"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = ORDER_FULFILLMENT.
   */
  @Test
  public void allowForHeaderWithOrderFulfillment() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "ORDER_FULFILLMENT"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = PAYMENT_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithPaymentConfirmation() {
    // Дано:
    when(stompFrame.getHeaders())
        .thenReturn(Collections.singletonMap("Status", "PAYMENT_CONFIRMATION"));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }
}