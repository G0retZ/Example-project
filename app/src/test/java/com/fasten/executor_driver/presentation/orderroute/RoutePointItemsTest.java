package com.fasten.executor_driver.presentation.orderroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoutePointItemsTest {

  private RoutePointItems routePointItems;
  @Mock
  private RoutePointItem rPItem;
  @Mock
  private RoutePointItem rPItem1;
  @Mock
  private RoutePointItem rPItem2;
  @Mock
  private RoutePointItem rPItem3;
  @Mock
  private RoutePointItem rPItem4;

  @Before
  public void setUp() {
    routePointItems = new RoutePointItems(
        Arrays.asList(rPItem, rPItem1, rPItem2, rPItem3, rPItem4)
    );
  }

  @Test
  public void testGetters() {
    // Дано:
    when(rPItem.isChecked()).thenReturn(true);
    when(rPItem1.isChecked()).thenReturn(true);

    // Результат:
    assertEquals(routePointItems.size(), 5);
    assertEquals(routePointItems.get(0), rPItem);
    assertEquals(routePointItems.get(1), rPItem1);
    assertEquals(routePointItems.get(2), rPItem2);
    assertEquals(routePointItems.get(3), rPItem3);
    assertEquals(routePointItems.get(4), rPItem4);
    assertFalse(routePointItems.isInProgress(rPItem));
    assertFalse(routePointItems.isInProgress(rPItem1));
    assertTrue(routePointItems.isInProgress(rPItem2));
    assertFalse(routePointItems.isInProgress(rPItem3));
    assertFalse(routePointItems.isInProgress(rPItem4));
  }

  @Test
  public void testEquals() {
    assertEquals(routePointItems,
        new RoutePointItems(
            Arrays.asList(rPItem, rPItem1, rPItem2, rPItem3, rPItem4)
        )
    );
    assertNotEquals(routePointItems,
        new RoutePointItems(
            Arrays.asList(rPItem2, rPItem1, rPItem3, rPItem, rPItem4)
        )
    );
    assertNotEquals(routePointItems,
        new RoutePointItems(
            Arrays.asList(rPItem, rPItem1, rPItem2, rPItem4)
        )
    );
    assertNotEquals(routePointItems,
        new RoutePointItems(new ArrayList<>())
    );
  }
}