package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.utils.TimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationTimeoutItemTest {

  private OrderConfirmationTimeoutItem ocTimeoutItem;

  @Mock
  private TimeUtils timeUtils;
  @Mock
  private TimeUtils timeUtils2;

  @Before
  public void setUp() {
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    ocTimeoutItem = new OrderConfirmationTimeoutItem(20_000, timeUtils);
  }

  @Test
  public void testGetItemTimestamp() {
    // Результат:
    assertEquals(ocTimeoutItem.getItemTimestamp(), 12390182L);
    assertEquals(ocTimeoutItem.getItemTimestamp(), 12390182L);
    assertEquals(ocTimeoutItem.getItemTimestamp(), 12390182L);
  }

  @Test
  public void testGetTimeout() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12395182L, 12400182L);

    // Результат:
    assertEquals(ocTimeoutItem.getTimeout(), 15000L);
    assertEquals(ocTimeoutItem.getTimeout(), 10000L);
  }

  @Test
  public void testEquals() {
    assertEquals(ocTimeoutItem, new OrderConfirmationTimeoutItem(20_000, timeUtils));
    assertNotEquals(ocTimeoutItem, new OrderConfirmationTimeoutItem(20_000, timeUtils2));
    assertNotEquals(ocTimeoutItem, new OrderConfirmationTimeoutItem(20_001, timeUtils));
    assertNotEquals(ocTimeoutItem, "");
    assertNotEquals(ocTimeoutItem, null);
  }
}