package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsFilterTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private OrderCostDetailsFilter filter;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    filter = new OrderCostDetailsFilter();
  }

  /* Проверяем работу с маппером */

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
  public void doNotTouchMapperForStatusHeaderWithWrongValue() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("");

    // Действие и Результат:
    assertFalse(filter.test(stompMessage));
  }

  /**
   * Должен пропустить если сообщение с заголовком Status с верным занчением.
   */
  @Test
  public void askForMappingForData() {
    // Дано:
    when(stompMessage.findHeader("Status")).thenReturn("PAYMENT_CONFIRMATION");

    // Действие и Результат:
    assertTrue(filter.test(stompMessage));
  }
}