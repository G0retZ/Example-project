package com.cargopull.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class ApiOrderTest {

  @Mock
  private ApiDriverDistancePair apiDriverDistancePair;
  @Mock
  private ApiOrderService apiOrderService;
  @Mock
  private ApiRoutePoint apiRoutePoint;
  @Mock
  private ApiRoutePoint apiRoutePoint1;
  @Mock
  private ApiOptionItem apiOptionItem;
  @Mock
  private ApiOptionItem apiOptionItem1;

  @Test
  public void testConstructor() {
    // Given:
    ApiOrder apiOrder = new ApiOrder(
        432,
        "CASH",
        "More than $3000",
        3000,
        219078901,
        23400,
        7300,
        "some comment",
        23,
        1200,
        1234567890,
        9876543210L,
        8197239812739L,
        apiDriverDistancePair,
        apiOrderService,
        "POLYGON",
        Arrays.asList(apiRoutePoint, apiRoutePoint1),
        Arrays.asList(apiOptionItem, apiOptionItem1)
    );

    // Effect
    assertEquals(apiOrder.getId(), 432);
    assertEquals(apiOrder.getPaymentType(), "CASH");
    assertEquals(apiOrder.getEstimatedAmountText(), "More than $3000");
    assertEquals(apiOrder.getEstimatedAmount(), 3000);
    assertEquals(apiOrder.getEstimatedTime(), 219078901);
    assertEquals(apiOrder.getEstimatedRouteDistance(), 23400);
    assertEquals(apiOrder.getTotalAmount(), 7300);
    assertEquals(apiOrder.getComment(), "some comment");
    assertEquals(apiOrder.getTimeout(), 23);
    assertEquals(apiOrder.getEtaToStartPoint(), 1200);
    assertEquals(apiOrder.getConfirmationTime(), 1234567890);
    assertEquals(apiOrder.getStartTime(), 9876543210L);
    assertEquals(apiOrder.getScheduledStartTime(), 8197239812739L);
    assertNotNull(apiOrder.getExecutorDistance());
    assertEquals(apiOrder.getExecutorDistance(), apiDriverDistancePair);
    assertEquals(apiOrder.getApiOrderService(), apiOrderService);
    assertEquals(apiOrder.getRouteType(), "POLYGON");
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