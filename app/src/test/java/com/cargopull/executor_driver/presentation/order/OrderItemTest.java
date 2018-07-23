package com.cargopull.executor_driver.presentation.order;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import com.cargopull.executor_driver.utils.TimeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderItemTest {

  private OrderItem orderItem;

  @Mock
  private Order order;
  @Mock
  private Order order2;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private TimeUtils timeUtils2;

  @Before
  public void setUp() {
    when(timeUtils.currentTimeMillis())
        .thenReturn(12390182L, 12390182L, 12395182L, 12400182L, 12395182L, 12400182L);
    orderItem = new OrderItem(order, timeUtils);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEtaToStartPoint()).thenReturn(358000L);
    when(order.getConfirmationTime()).thenReturn(12384000L);
    when(order.getDistance()).thenReturn(12239L);
    when(order.getEstimatedRouteLength()).thenReturn(33239L);
    when(order.getEstimatedTime()).thenReturn(3324339L);
    when(order.getComment()).thenReturn("com");
    when(order.getServiceName()).thenReturn("service");
    when(order.getEstimatedPriceText()).thenReturn("7000");
    when(order.getEstimatedPrice()).thenReturn(7000L);
    when(order.getTimeout()).thenReturn(20_000L);
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", true, false),
        new OptionBoolean(3, "bool4", "bd", true, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", true, 7, 0, 5)
    )));
    when(routePoint.getAddress()).thenReturn("add0");
    when(routePoint.getComment()).thenReturn("comment0");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint1.getAddress()).thenReturn("add1");
    when(routePoint1.getComment()).thenReturn("comment1");
    when(routePoint1.getLatitude()).thenReturn(15.421);
    when(routePoint1.getLongitude()).thenReturn(20.2341);
    when(routePoint2.getAddress()).thenReturn("add2");
  }

  @Test
  public void testGettersForAllQueued() {
    // Результат:
    assertEquals(orderItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(orderItem.getCoordinatesString(), "5.421,10.2341");
    assertEquals(orderItem.getNextAddress(), "add0");
    assertEquals(orderItem.getNextAddressComment(), "comment0");
    assertEquals(orderItem.getLastAddress(), "add2");
    assertEquals(orderItem.getDistance(),
        String.format(Locale.getDefault(), "%.2f", 12.24f));
    assertEquals(orderItem.getOrderComment(), "com");
    assertEquals(orderItem.getServiceName(), "service");
    assertEquals(orderItem.getEstimatedPriceText(), "7000");
    assertEquals(orderItem.getEstimatedPrice(), 7000);
    assertEquals(orderItem.getRoutePointsCount(), 3);
    assertEquals(orderItem.getRouteLength(),
        String.format(Locale.getDefault(), "%.2f", 33239 / 1000d));
    assertEquals(orderItem.getEstimatedTimeSeconds(), 3324);
    assertEquals(orderItem.getOrderOptionsRequired(), "bool2\nbool4\nnum1: 3\nnum2: 7");
    assertEquals(orderItem.getEtaSeconds(), 358);
    assertEquals(orderItem.getSecondsToMeetClient(), 352);
    assertEquals(orderItem.getSecondsToMeetClient(), 347);
    assertEquals(orderItem.getSecondsToMeetClient(), 342);
    assertArrayEquals(orderItem.getProgressLeft(), new long[]{75, 15000});
    assertArrayEquals(orderItem.getProgressLeft(), new long[]{50, 10000});
  }

  @Test
  public void testGettersForAllClosed() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Результат:
    assertEquals(orderItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(orderItem.getCoordinatesString(), "5.421,10.2341");
    assertEquals(orderItem.getNextAddress(), "add0");
    assertEquals(orderItem.getNextAddressComment(), "comment0");
    assertEquals(orderItem.getLastAddress(), "add2");
    assertEquals(orderItem.getDistance(),
        String.format(Locale.getDefault(), "%.2f", 12.24f));
    assertEquals(orderItem.getOrderComment(), "com");
    assertEquals(orderItem.getServiceName(), "service");
    assertEquals(orderItem.getEstimatedPriceText(), "7000");
    assertEquals(orderItem.getEstimatedPrice(), 7000);
    assertEquals(orderItem.getRoutePointsCount(), 3);
    assertEquals(orderItem.getRouteLength(),
        String.format(Locale.getDefault(), "%.2f", 33239 / 1000d));
    assertEquals(orderItem.getEstimatedTimeSeconds(), 3324);
    assertEquals(orderItem.getOrderOptionsRequired(), "bool2\nbool4\nnum1: 3\nnum2: 7");
    assertEquals(orderItem.getEtaSeconds(), 358);
    assertEquals(orderItem.getSecondsToMeetClient(), 352);
    assertEquals(orderItem.getSecondsToMeetClient(), 347);
    assertEquals(orderItem.getSecondsToMeetClient(), 342);
    assertArrayEquals(orderItem.getProgressLeft(), new long[]{75, 15000});
    assertArrayEquals(orderItem.getProgressLeft(), new long[]{50, 10000});
  }

  @Test
  public void testGettersForSecondActive() {
    // Дано:
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);

    // Результат:
    assertEquals(orderItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=15.421,20.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
    assertEquals(orderItem.getCoordinatesString(), "15.421,20.2341");
    assertEquals(orderItem.getNextAddress(), "add1");
    assertEquals(orderItem.getNextAddressComment(), "comment1");
    assertEquals(orderItem.getLastAddress(), "add2");
    assertEquals(orderItem.getDistance(),
        String.format(Locale.getDefault(), "%.2f", 12.24f));
    assertEquals(orderItem.getOrderComment(), "com");
    assertEquals(orderItem.getServiceName(), "service");
    assertEquals(orderItem.getEstimatedPriceText(), "7000");
    assertEquals(orderItem.getEstimatedPrice(), 7000);
    assertEquals(orderItem.getRoutePointsCount(), 3);
    assertEquals(orderItem.getRouteLength(),
        String.format(Locale.getDefault(), "%.2f", 33239 / 1000d));
    assertEquals(orderItem.getEstimatedTimeSeconds(), 3324);
    assertEquals(orderItem.getOrderOptionsRequired(), "bool2\nbool4\nnum1: 3\nnum2: 7");
    assertEquals(orderItem.getEtaSeconds(), 358);
    assertEquals(orderItem.getSecondsToMeetClient(), 352);
    assertEquals(orderItem.getSecondsToMeetClient(), 347);
    assertEquals(orderItem.getSecondsToMeetClient(), 342);
    assertArrayEquals(orderItem.getProgressLeft(), new long[]{75, 15000});
    assertArrayEquals(orderItem.getProgressLeft(), new long[]{50, 10000});
  }

  @Test
  public void testEquals() {
    assertEquals(orderItem, new OrderItem(order, timeUtils));
    assertEquals(orderItem, new OrderItem(order, timeUtils2));
    assertNotEquals(orderItem, new OrderItem(order2, timeUtils));
  }
}