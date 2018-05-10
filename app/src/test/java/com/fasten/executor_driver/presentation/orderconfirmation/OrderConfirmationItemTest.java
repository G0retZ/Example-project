package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.RoutePoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationItemTest {

  private OrderConfirmationItem orderConfirmationItem;

  @Mock
  private Offer offer;
  @Mock
  private Offer offer2;
  @Mock
  private RoutePoint routePoint;

  @Before
  public void setUp() {
    orderConfirmationItem = new OrderConfirmationItem(offer);
  }

  @Test
  public void testGetters() {
    // Дано:
    when(offer.getRoutePoint()).thenReturn(routePoint);
    when(offer.getDistance()).thenReturn(12239L);
    when(offer.getComment()).thenReturn("com");
    when(offer.getEstimatedPrice()).thenReturn("7000");
    when(offer.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", true, false),
        new OptionBoolean(3, "bool4", "bd", true, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", true, 7, 0, 5)
    )));
    when(routePoint.getAddress()).thenReturn("add");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(orderConfirmationItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=360x304&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(orderConfirmationItem.getPrice(), "7000");
    assertEquals(orderConfirmationItem.getAddress(), "add");
    assertEquals(orderConfirmationItem.getDistance(),
        String.format(Locale.getDefault(), "%.2f", 12.24f));
    assertEquals(orderConfirmationItem.getOfferComment(), "com");
    assertEquals(orderConfirmationItem.getEstimatedPrice(), "7000");
    assertEquals(orderConfirmationItem.getOrderOptionsRequired(),
        "bool2\nbool4\nnum1: 3\nnum2: 7\n");
  }

  @Test
  public void testEquals() {
    assertEquals(orderConfirmationItem, new OrderConfirmationItem(offer));
    assertNotEquals(orderConfirmationItem, new OrderConfirmationItem(offer2));
  }
}