package com.fasten.executor_driver.backend.websocket.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class ApiOrderTest {

  private ApiOrder apiOrder;

  @Before
  public void setUp() {
    apiOrder = new ApiOrder(
        432,
        "More than $3000",
        5000, 2300, "some comment",
        23,
        1200,
        1234567890,
        9876543210L, new ApiDriverDistancePair(32),
        Arrays.asList(
            new ApiRoutePoint(5, 123, 456, "point comment", "address", true),
            new ApiRoutePoint(6, 321, 654, "comment point", "rest", false)
        ),
        Arrays.asList(
            new ApiOptionItem(12, "name", "description", false, false, "false", null, null),
            new ApiOptionItem(34, "no name", "wha?", true, true, "4", 0, 5)
        )
    );
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOrder.getId(), 432);
    assertEquals(apiOrder.getEstimatedAmount(), "More than $3000");
    assertEquals(apiOrder.getOrderCost(), 5000);
    assertEquals(apiOrder.getExcessCost(), 2300);
    assertEquals(apiOrder.getComment(), "some comment");
    assertEquals(apiOrder.getTimeout(), 23);
    assertEquals(apiOrder.getEtaToStartPoint(), 1200);
    assertEquals(apiOrder.getConfirmationTime(), 1234567890);
    assertEquals(apiOrder.getOrderStartTime(), 9876543210L);
    assertNotNull(apiOrder.getExecutorDistance());
    assertEquals(apiOrder.getExecutorDistance().getDistance(), 32);
    assertNotNull(apiOrder.getRoute());
    assertEquals(apiOrder.getRoute().size(), 2);
    assertEquals(apiOrder.getRoute().get(0).getId(), 5);
    assertEquals(apiOrder.getRoute().get(0).getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(apiOrder.getRoute().get(0).getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(apiOrder.getRoute().get(0).getComment(), "point comment");
    assertEquals(apiOrder.getRoute().get(0).getAddress(), "address");
    assertTrue(apiOrder.getRoute().get(0).isChecked());
    assertEquals(apiOrder.getRoute().get(1).getId(), 6);
    assertEquals(apiOrder.getRoute().get(1).getLatitude(), 321, Double.MIN_VALUE);
    assertEquals(apiOrder.getRoute().get(1).getLongitude(), 654, Double.MIN_VALUE);
    assertEquals(apiOrder.getRoute().get(1).getComment(), "comment point");
    assertEquals(apiOrder.getRoute().get(1).getAddress(), "rest");
    assertFalse(apiOrder.getRoute().get(1).isChecked());
    assertNotNull(apiOrder.getOptions());
    assertEquals(apiOrder.getOptions().size(), 2);
    assertEquals(apiOrder.getOptions().get(0).getId(), 12);
    assertEquals(apiOrder.getOptions().get(0).getName(), "name");
    assertEquals(apiOrder.getOptions().get(0).getDescription(), "description");
    assertFalse(apiOrder.getOptions().get(0).isNumeric());
    assertFalse(apiOrder.getOptions().get(0).isDynamic());
    assertEquals(apiOrder.getOptions().get(0).getValue(), "false");
    assertNull(apiOrder.getOptions().get(0).getMinValue());
    assertNull(apiOrder.getOptions().get(0).getMaxValue());
    assertEquals(apiOrder.getOptions().get(1).getId(), 34);
    assertEquals(apiOrder.getOptions().get(1).getName(), "no name");
    assertEquals(apiOrder.getOptions().get(1).getDescription(), "wha?");
    assertTrue(apiOrder.getOptions().get(1).isNumeric());
    assertTrue(apiOrder.getOptions().get(1).isDynamic());
    assertEquals(apiOrder.getOptions().get(1).getValue(), "4");
    assertEquals(apiOrder.getOptions().get(1).getMinValue(), new Integer(0));
    assertEquals(apiOrder.getOptions().get(1).getMaxValue(), new Integer(5));
  }
}