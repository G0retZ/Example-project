package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class OfferTest {

  private Offer offer;
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    routePoint = new RoutePoint(10, 5, "com", "add");
    offer = new Offer(7, "com", 1200239, "7000", 20, 600, 1234567890, routePoint);
  }

  @Test
  public void testConstructor() {
    assertEquals(offer.getId(), 7);
    assertEquals(offer.getComment(), "com");
    assertEquals(offer.getDistance(), 1200239);
    assertEquals(offer.getEstimatedPrice(), "7000");
    assertEquals(offer.getTimeout(), 20);
    assertEquals(offer.getEta(), 600);
    assertEquals(offer.getTimeStamp(), 1234567890);
    assertEquals(offer.getRoutePoint(), routePoint);
    assertEquals(offer.getOptions(), new ArrayList<Option>());
  }

  @Test
  public void testSetOptions() {
    offer.setOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    offer.setOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertEquals(offer.getOptions(), new ArrayList<Option>(
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
    offer.addOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    offer.addOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertEquals(offer.getOptions(), new ArrayList<Option>(
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