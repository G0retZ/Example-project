package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderTest {

  private Order order;
  @Mock
  private OptionNumeric option;
  @Mock
  private OptionNumeric option1;
  @Mock
  private OptionBoolean option2;
  @Mock
  private OptionBoolean option3;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;
  @Mock
  private RoutePoint routePoint3;

  @Before
  public void setUp() {
    order = new Order(7, PaymentType.CASH, "com", "service", 1200239, "7000", 7000, 7728_192_819L,
        28_020, 9400, 20, 600, 1234567890, 9876543210L, 123812983712L);
    order.addOptions(option);
    order.addRoutePoints(routePoint);
  }

  @Test
  public void testConstructor() {
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 1200239);
    assertEquals(order.getEstimatedPriceText(), "7000");
    assertEquals(order.getEstimatedPrice(), 7000);
    assertEquals(order.getEstimatedTime(), 7728_192_819L);
    assertEquals(order.getEstimatedRouteLength(), 28_020);
    assertEquals(order.getTotalCost(), 9400);
    assertEquals(order.getTimeout(), 20);
    assertEquals(order.getEtaToStartPoint(), 600);
    assertEquals(order.getConfirmationTime(), 1234567890);
    assertEquals(order.getStartTime(), 9876543210L);
    assertEquals(order.getScheduledStartTime(), 123812983712L);
    assertEquals(order.getRoutePath(), Collections.singletonList(routePoint));
    assertEquals(order.getNextActiveRoutePoint(), routePoint);
    assertEquals(order.getOptions(), Collections.singletonList(option));
  }

  @Test
  public void testSetOptions() {
    order.setOptions(option1, option2, option3);
    order.setOptions(option3, option1, option2);
    assertEquals(order.getOptions(), new ArrayList<>(
        Arrays.asList(option3, option1, option2)
    ));
  }

  @Test
  public void testAddOptions() {
    order.addOptions(option1, option2, option3);
    order.addOptions(option3, option1, option2);
    assertEquals(order.getOptions(), new ArrayList<>(
        Arrays.asList(option, option1, option2, option3, option3, option1, option2)
    ));
  }

  @Test
  public void testSetRoutePoints() {
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    order.setRoutePoints(routePoint1, routePoint2, routePoint3);
    order.setRoutePoints(routePoint3, routePoint2, routePoint1);
    assertEquals(order.getRoutePath(), new ArrayList<>(
        Arrays.asList(routePoint3, routePoint2, routePoint1)
    ));
    assertEquals(order.getNextActiveRoutePoint(), routePoint2);
  }

  @Test
  public void testAddRoutePoints() {
    order.addRoutePoints(routePoint1, routePoint2, routePoint3);
    order.addRoutePoints(routePoint3, routePoint2, routePoint1);
    assertEquals(order.getRoutePath(), new ArrayList<>(
        Arrays.asList(routePoint, routePoint1, routePoint2, routePoint3, routePoint3, routePoint2,
            routePoint1)
    ));
    assertEquals(order.getNextActiveRoutePoint(), routePoint);
  }

  @Test
  public void testWithEtaToStartPoint() {
    // Дано:
    order.setOptions(option1, option2, option3);
    order.setRoutePoints(routePoint1, routePoint2, routePoint3);

    // Действие:
    Order order1 = order.withEtaToStartPoint(800);

    // Результат:
    assertEquals(order1.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
    assertEquals(order1.getComment(), "com");
    assertEquals(order1.getServiceName(), "service");
    assertEquals(order1.getDistance(), 1200239);
    assertEquals(order1.getEstimatedPriceText(), "7000");
    assertEquals(order1.getEstimatedPrice(), 7000);
    assertEquals(order1.getEstimatedTime(), 7728_192_819L);
    assertEquals(order1.getEstimatedRouteLength(), 28_020);
    assertEquals(order1.getTotalCost(), 9400);
    assertEquals(order1.getTimeout(), 20);
    assertEquals(order1.getEtaToStartPoint(), 800);
    assertEquals(order1.getConfirmationTime(), 1234567890);
    assertEquals(order1.getStartTime(), 9876543210L);
    assertEquals(order1.getScheduledStartTime(), 123812983712L);
    assertEquals(order1.getRoutePath(), new ArrayList<>(
        Arrays.asList(routePoint1, routePoint2, routePoint3)
    ));
    assertEquals(order1.getOptions(), new ArrayList<>(
        Arrays.asList(option1, option2, option3)
    ));
  }

  @Test
  public void testEquals() {
    Order order1 = new Order(7, PaymentType.CASH, "com", "service", 1200239, "7000", 7000, 7728_192_819L,
        28_020, 9400, 20, 600, 1234567890, 9876543210L, 123812983712L);
    assertEquals(order, order);
    assertEquals(order, order1);
    order1 = new Order(7, PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
    assertEquals(order, order1);
    order1 = new Order(7, PaymentType.CONTRACT, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
    assertEquals(order, order1);
    order1 = new Order(7, PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
    order1.setOptions(option1, option2, option3);
    assertEquals(order, order1);
    order1 = new Order(7, PaymentType.CASH, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
    order1.setRoutePoints(routePoint1, routePoint2, routePoint3);
    assertEquals(order, order1);
    order1.addRoutePoints(routePoint1, routePoint2, routePoint3);
    assertEquals(order, order1);
    order1.setOptions(option1, option2, option3);
    assertEquals(order, order1);
    order1 = new Order(6, PaymentType.CONTRACT, "com", "service", 1200239, "7000", 7000, 7728_192_819L,
        28_020,
        9400, 20, 600, 1234567890, 9876543210L, 123812983712L);
    assertNotEquals(order, order1);
    assertNotEquals(order, "");
    assertNotEquals(order, null);
  }

  @Test
  public void testHashCode() {
    assertEquals(order.hashCode(), 7);
    assertNotEquals(order.hashCode(), 8);
  }
}