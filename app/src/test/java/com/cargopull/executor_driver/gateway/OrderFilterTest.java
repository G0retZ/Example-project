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
public class OrderFilterTest {

  private OrderFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new OrderFilter();
  }

  /**
   * Должен отказать, если статус не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() {
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
   * Должен пропустить, если сообщение с заголовком Status = DRIVER_ORDER_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithDriverOrderConfirmation() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("DRIVER_ORDER_CONFIRMATION");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = CLIENT_ORDER_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithClientOrderConfirmation() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("CLIENT_ORDER_CONFIRMATION");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = MOVING_TO_CLIENT.
   */
  @Test
  public void allowForHeaderWithMovingToClient() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("MOVING_TO_CLIENT");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = WAITING_FOR_CLIENT.
   */
  @Test
  public void allowForHeaderWithWaitingForClient() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("WAITING_FOR_CLIENT");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = ORDER_FULFILLMENT.
   */
  @Test
  public void allowForHeaderWithOrderFulfillment() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("ORDER_FULFILLMENT");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }

  /**
   * Должен пропустить, если сообщение с заголовком Status = PAYMENT_CONFIRMATION.
   */
  @Test
  public void allowForHeaderWithPaymentConfirmation() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("PAYMENT_CONFIRMATION");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}