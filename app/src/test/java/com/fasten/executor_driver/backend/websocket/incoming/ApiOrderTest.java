package com.fasten.executor_driver.backend.websocket.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiOrderTest {

  private ApiOrder apiOrder;
  @Mock
  private ApiRoutePoint apiRoutePoint;
  @Mock
  private ApiRoutePoint apiRoutePoint1;
  @Mock
  private ApiOptionItem apiOptionItem;
  @Mock
  private ApiOptionItem apiOptionItem1;

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
        Arrays.asList(apiRoutePoint, apiRoutePoint1),
        Arrays.asList(apiOptionItem, apiOptionItem1)
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
    assertEquals(apiOrder.getRoute().get(0), apiRoutePoint);
    assertEquals(apiOrder.getRoute().get(1), apiRoutePoint1);
    assertNotNull(apiOrder.getOptions());
    assertEquals(apiOrder.getOptions().size(), 2);
    assertEquals(apiOrder.getOptions().get(0), apiOptionItem);
    assertEquals(apiOrder.getOptions().get(1), apiOptionItem1);
  }
}