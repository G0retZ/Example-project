package com.cargopull.executor_driver.backend.websocket.outgoing;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.CancelOrderReason;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiCancelOrderReasonTest {

  @Mock
  private CancelOrderReason cancelOrderReason;

  @Test
  public void testConstructor() {
    // Дано:
    when(cancelOrderReason.getId()).thenReturn(7);
    when(cancelOrderReason.getName()).thenReturn("seven");
    when(cancelOrderReason.getUnusedName()).thenReturn("unused");

    // Действие:
    ApiCancelOrderReason apiCancelOrderReason = new ApiCancelOrderReason(cancelOrderReason);

    // Результат:
    assertEquals(apiCancelOrderReason.getId(), 7);
    assertEquals(apiCancelOrderReason.getDescription(), "seven");
    assertEquals(apiCancelOrderReason.getName(), "unused");
  }
}