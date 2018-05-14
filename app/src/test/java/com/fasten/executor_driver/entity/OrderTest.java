package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class OrderTest {

  private Order order;
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    routePoint = new RoutePoint(10, 5, "com", "add");
    order = new Order(7, "com", 1200239, "7000", 20, 600, 1234567890, routePoint);
  }

  @Test
  public void testConstructor() {
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getDistance(), 1200239);
    assertEquals(order.getEstimatedPrice(), "7000");
    assertEquals(order.getTimeout(), 20);
    assertEquals(order.getEtaToStartPoint(), 600);
    assertEquals(order.getConfirmationTime(), 1234567890);
    assertEquals(order.getRoutePoint(), routePoint);
    assertEquals(order.getOptions(), new ArrayList<Option>());
  }

  @Test
  public void testSetOptions() {
    order.setOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    order.setOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertEquals(order.getOptions(), new ArrayList<Option>(
        Arrays.asList(
            new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "description2", true, false),
            new OptionBoolean(3, "name3", "description3", false, true)
        )
    ));
  }

  @Test
  public void testAddOptions() {
    order.addOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    order.addOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertEquals(order.getOptions(), new ArrayList<Option>(
        Arrays.asList(
            new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "description2", true, false),
            new OptionBoolean(3, "name3", "description3", false, true),
            new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "description2", true, false),
            new OptionBoolean(3, "name3", "description3", false, true)
        )
    ));
  }
}