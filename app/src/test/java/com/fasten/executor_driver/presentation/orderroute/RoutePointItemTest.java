package com.fasten.executor_driver.presentation.orderroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.RoutePoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoutePointItemTest {

  private RoutePointItem routeItem;

  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;

  @Before
  public void setUp() {
    routeItem = new RoutePointItem(routePoint);
  }

  @Test
  public void testGetters() {
    // Дано:
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.isChecked()).thenReturn(true);

    // Результат:
    assertTrue(routeItem.isChecked());
    assertEquals(routeItem.getAddress(), "add");
  }

  @Test
  public void testEquals() {
    assertEquals(routeItem, new RoutePointItem(routePoint));
    assertNotEquals(routeItem, new RoutePointItem(routePoint1));
  }
}