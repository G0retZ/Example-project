package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;

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
  private OptionNumeric option0;
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
    order = new Order(7, "com", "service", 1200239, "7000", 7000, 7728_192_819L, 28_020,
        9400, 20, 600, 1234567890, 9876543210L);
    order.addRoutePoints(routePoint);
  }

  @Test
  public void testConstructor() {
    assertEquals(order.getId(), 7);
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
    assertEquals(order.getOrderStartTime(), 9876543210L);
    assertEquals(order.getRoutePath(), Collections.singletonList(routePoint));
    assertEquals(order.getOptions(), new ArrayList<Option>());
    assertEquals(order.getRoutePath(), Collections.singletonList(routePoint));
  }

  @Test
  public void testSetOptions() {
    order.setOptions(option0, option1, option2, option3);
    order.setOptions(option3, option1, option0, option2);
    assertEquals(order.getOptions(), new ArrayList<>(
        Arrays.asList(option3, option1, option0, option2)
    ));
  }

  @Test
  public void testAddOptions() {
    order.addOptions(option0, option1, option2, option3);
    order.addOptions(option3, option1, option0, option2);
    assertEquals(order.getOptions(), new ArrayList<>(
        Arrays.asList(option0, option1, option2, option3, option3, option1, option0, option2)
    ));
  }

  @Test
  public void testSetRoutePoints() {
    order.setRoutePoints(routePoint1, routePoint2, routePoint3);
    order.setRoutePoints(routePoint3, routePoint2, routePoint1);
    assertEquals(order.getRoutePath(), new ArrayList<>(
        Arrays.asList(routePoint3, routePoint2, routePoint1)
    ));
  }

  @Test
  public void testAddRoutePoints() {
    order.addRoutePoints(routePoint1, routePoint2, routePoint3);
    order.addRoutePoints(routePoint3, routePoint2, routePoint1);
    assertEquals(order.getRoutePath(), new ArrayList<>(
        Arrays.asList(routePoint, routePoint1, routePoint2, routePoint3, routePoint3, routePoint2,
            routePoint1)
    ));
  }
}