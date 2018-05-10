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

public class ApiOfferTest {

  private ApiOffer apiOffer;

  @Before
  public void setUp() {
    apiOffer = new ApiOffer(
        432,
        "More than $3000",
        "some comment",
        23,
        new ApiDriverDistancePair(32),
        Arrays.asList(
            new ApiRoutePoint(123, 456, "point comment", "address"),
            new ApiRoutePoint(321, 654, "comment point", "rest")
        ),
        Arrays.asList(
            new ApiOptionItem(12, "name", "description", false, false, "false", null, null),
            new ApiOptionItem(34, "no name", "wha?", true, true, "4", 0, 5)
        )
    );
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOffer.getId(), 432);
    assertEquals(apiOffer.getEstimatedAmount(), "More than $3000");
    assertEquals(apiOffer.getComment(), "some comment");
    assertEquals(apiOffer.getTimeout(), 23);
    assertNotNull(apiOffer.getExecutorDistance());
    assertEquals(apiOffer.getExecutorDistance().getDistance(), 32);
    assertNotNull(apiOffer.getRoute());
    assertEquals(apiOffer.getRoute().size(), 2);
    assertEquals(apiOffer.getRoute().get(0).getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(apiOffer.getRoute().get(0).getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(apiOffer.getRoute().get(0).getComment(), "point comment");
    assertEquals(apiOffer.getRoute().get(0).getAddress(), "address");
    assertEquals(apiOffer.getRoute().get(1).getLatitude(), 321, Double.MIN_VALUE);
    assertEquals(apiOffer.getRoute().get(1).getLongitude(), 654, Double.MIN_VALUE);
    assertEquals(apiOffer.getRoute().get(1).getComment(), "comment point");
    assertEquals(apiOffer.getRoute().get(1).getAddress(), "rest");
    assertNotNull(apiOffer.getOptions());
    assertEquals(apiOffer.getOptions().size(), 2);
    assertEquals(apiOffer.getOptions().get(0).getId(), 12);
    assertEquals(apiOffer.getOptions().get(0).getName(), "name");
    assertEquals(apiOffer.getOptions().get(0).getDescription(), "description");
    assertFalse(apiOffer.getOptions().get(0).isNumeric());
    assertFalse(apiOffer.getOptions().get(0).isDynamic());
    assertEquals(apiOffer.getOptions().get(0).getValue(), "false");
    assertNull(apiOffer.getOptions().get(0).getMinValue());
    assertNull(apiOffer.getOptions().get(0).getMaxValue());
    assertEquals(apiOffer.getOptions().get(1).getId(), 34);
    assertEquals(apiOffer.getOptions().get(1).getName(), "no name");
    assertEquals(apiOffer.getOptions().get(1).getDescription(), "wha?");
    assertTrue(apiOffer.getOptions().get(1).isNumeric());
    assertTrue(apiOffer.getOptions().get(1).isDynamic());
    assertEquals(apiOffer.getOptions().get(1).getValue(), "4");
    assertEquals(apiOffer.getOptions().get(1).getMinValue(), new Integer(0));
    assertEquals(apiOffer.getOptions().get(1).getMaxValue(), new Integer(5));
  }
}