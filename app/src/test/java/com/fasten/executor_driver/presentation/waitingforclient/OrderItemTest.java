package com.fasten.executor_driver.presentation.waitingforclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Order;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderItemTest {

  private OrderItem orderItem;

  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    orderItem = new OrderItem(order);
  }

  @Test
  public void testGetters() {
    // Дано:
    when(order.getComment()).thenReturn("com");
    when(order.getEstimatedPrice()).thenReturn("7000");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", true, false),
        new OptionBoolean(3, "bool4", "bd", true, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", true, 7, 0, 5)
    )));

    // Результат:
    assertEquals(orderItem.getOrderComment(), "com");
    assertEquals(orderItem.getEstimatedPrice(), "7000");
    assertEquals(orderItem.getOrderOptionsRequired(),
        "bool2\nbool4\nnum1: 3\nnum2: 7");
  }

  @Test
  public void testEquals() {
    assertEquals(orderItem, new OrderItem(order));
    assertNotEquals(orderItem, new OrderItem(order2));
  }
}