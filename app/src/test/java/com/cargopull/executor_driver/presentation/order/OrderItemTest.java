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
import org.joda.time.format.DateTimeFormat;
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
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    orderItem = new OrderItem(order, timeUtils);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
//    when(routePoint1.getLatitude()).thenReturn(15.421);
//    when(routePoint1.getLongitude()).thenReturn(20.2341);
  }

  @Test
  public void testGetLoadPointMapUrlIfAllQueued() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(orderItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
  }

  @Test
  public void testGetLoadPointMapUrlIfAllClosed() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(orderItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=5.421,10.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
  }

  @Test
  public void testGetLoadPointMapUrlIfSecondActive() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(routePoint1.getLatitude()).thenReturn(15.421);
    when(routePoint1.getLongitude()).thenReturn(20.2341);

    // Результат:
    assertEquals(orderItem.getLoadPointMapUrl(),
        "https://maps.googleapis.com/maps/api/staticmap?center=15.421,20.2341&zoom=16&size=360x200&maptype=roadmap&key=AIzaSyC20FZNHJqrQH5UhypeUy3thpqII33QBPI");
  }

  @Test
  public void testGetCoordinatesStringIfAllQueued() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(orderItem.getCoordinatesString(), "5.421,10.2341");
  }

  @Test
  public void testGetCoordinatesStringIfAllClosed() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);

    // Результат:
    assertEquals(orderItem.getCoordinatesString(), "5.421,10.2341");
  }

  @Test
  public void testGetCoordinatesStringIfSecondActive() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(routePoint1.getLatitude()).thenReturn(15.421);
    when(routePoint1.getLongitude()).thenReturn(20.2341);

    // Результат:
    assertEquals(orderItem.getCoordinatesString(), "15.421,20.2341");
  }

  @Test
  public void testGetNextAddressIfAllQueued() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint.getAddress()).thenReturn("add0");

    // Результат:
    assertEquals(orderItem.getNextAddress(), "add0");
  }

  @Test
  public void testGetNextAddressIfAllClosed() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint.getAddress()).thenReturn("add0");

    // Результат:
    assertEquals(orderItem.getNextAddress(), "add0");
  }

  @Test
  public void testGetNextAddressIfSecondActive() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(routePoint1.getAddress()).thenReturn("add1");

    // Результат:
    assertEquals(orderItem.getNextAddress(), "add1");
  }

  @Test
  public void testGetNextAddressCommentIfAllQueued() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint.getComment()).thenReturn("comment0");

    // Результат:
    assertEquals(orderItem.getNextAddressComment(), "comment0");
  }

  @Test
  public void testGetNextAddressCommentIfAllClosed() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint.getComment()).thenReturn("comment0");

    // Результат:
    assertEquals(orderItem.getNextAddressComment(), "comment0");
  }

  @Test
  public void testGetNextAddressCommentIfSecondActive() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(routePoint1.getComment()).thenReturn("comment1");

    // Результат:
    assertEquals(orderItem.getNextAddressComment(), "comment1");
  }

  @Test
  public void testGetLastAddress() {
    // Дано:
    when(routePoint2.getAddress()).thenReturn("add2");

    // Результат:
    assertEquals(orderItem.getLastAddress(), "add2");
  }

  @Test
  public void testGetDistance() {
    // Дано:
    when(order.getDistance()).thenReturn(12239);

    // Результат:
    assertEquals(orderItem.getDistance(),
        String.format(Locale.getDefault(), "%.2f", 12.24f));
  }

  @Test
  public void testGetOrderComment() {
    // Дано:
    when(order.getComment()).thenReturn("com");

    // Результат:
    assertEquals(orderItem.getOrderComment(), "com");
  }

  @Test
  public void testGetServiceName() {
    // Дано:
    when(order.getServiceName()).thenReturn("service");

    // Результат:
    assertEquals(orderItem.getServiceName(), "service");
  }

  @Test
  public void testGetEstimatedPriceText() {
    // Дано:
    when(order.getEstimatedPriceText()).thenReturn("7000");

    // Результат:
    assertEquals(orderItem.getEstimatedPriceText(), "7000");
  }

  @Test
  public void testGetEstimatedPrice() {
    // Дано:
    when(order.getEstimatedPrice()).thenReturn(7000L);

    // Результат:
    assertEquals(orderItem.getEstimatedPrice(), 7000);
  }

  @Test
  public void testGetRoutePointsCount() {
    // Результат:
    assertEquals(orderItem.getRoutePointsCount(), 3);
  }

  @Test
  public void testGetRouteLength() {
    // Дано:
    when(order.getEstimatedRouteLength()).thenReturn(33239L);

    // Результат:
    assertEquals(orderItem.getRouteLength(),
        String.format(Locale.getDefault(), "%.2f", 33239 / 1000d));
  }

  @Test
  public void testGetEstimatedTimeSeconds() {
    // Дано:
    when(order.getEstimatedTime()).thenReturn(3324339L);

    // Результат:
    assertEquals(orderItem.getEstimatedTimeSeconds(), 3324);
  }

  @Test
  public void testGetOccupationTimeInPast() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    when(order.getScheduledStartTime()).thenReturn(11792192L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);
    when(order.getEstimatedTime()).thenReturn(3324339L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("HH:mm").print(12748182L)
        + "–"
        + DateTimeFormat.forPattern("HH:mm").print(16072521L));
    assertEquals(orderItem.getOccupationTime(),
        DateTimeFormat.forPattern("HH:mm").print(12748182L)
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(16072521L));
  }

  @Test
  public void testGetOccupationTimeWithEtaStartsInPast() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    when(order.getScheduledStartTime()).thenReturn(12692192L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);
    when(order.getEstimatedTime()).thenReturn(3324339L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("HH:mm").print(12748182L)
        + "–"
        + DateTimeFormat.forPattern("HH:mm").print(16072521L));
    assertEquals(orderItem.getOccupationTime(),
        DateTimeFormat.forPattern("HH:mm").print(12748182L)
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(16072521L));
  }

  @Test
  public void testGetOccupationTimeInFuture() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    when(order.getScheduledStartTime()).thenReturn(22792192L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);
    when(order.getEstimatedTime()).thenReturn(3324339L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("HH:mm").print(22792192L)
        + "–"
        + DateTimeFormat.forPattern("HH:mm").print(26116531L));
    assertEquals(orderItem.getOccupationTime(),
        DateTimeFormat.forPattern("HH:mm").print(22792192L)
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(26116531L));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testGetOccupationDateInPast() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    when(order.getScheduledStartTime()).thenReturn(11792192L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d MMMM, EEEE").print(0));
    assertEquals(orderItem.getOccupationDate(), DateTimeFormat.forPattern("d MMMM, EEEE").print(0));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testGetOccupationDateWithEtaStartsInPast() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    when(order.getScheduledStartTime()).thenReturn(12692192L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d MMMM, EEEE").print(0));
    assertEquals(orderItem.getOccupationDate(), DateTimeFormat.forPattern("d MMMM, EEEE").print(0));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testGetOccupationDateInFuture() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12390182L);
    when(order.getScheduledStartTime()).thenReturn(22792192L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d MMMM, EEEE").print(0));
    assertEquals(orderItem.getOccupationDate(), DateTimeFormat.forPattern("d MMMM, EEEE").print(0));
  }

  @Test
  public void testGetOrderOptionsRequired() {
    // Дано:
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", true, false),
        new OptionBoolean(3, "bool4", "bd", true, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", true, 7, 0, 5)
    )));

    // Результат:
    assertEquals(orderItem.getOrderOptionsRequired(), "bool2\nbool4\nnum1: 3\nnum2: 7");
  }

  @Test
  public void testGetEtaSeconds() {
    // Дано:
    when(order.getEtaToStartPoint()).thenReturn(358000L);

    // Результат:
    assertEquals(orderItem.getEtaSeconds(), 358);
  }

  @Test
  public void testGetSecondsToMeetClient() {
    // Дано:
    when(order.getConfirmationTime()).thenReturn(12384000L);
    when(timeUtils.currentTimeMillis())
        .thenReturn(12390182L, 12395182L, 12400182L);
    when(order.getEtaToStartPoint()).thenReturn(358000L);

    // Результат:
    assertEquals(orderItem.getSecondsToMeetClient(), 352);
    assertEquals(orderItem.getSecondsToMeetClient(), 347);
    assertEquals(orderItem.getSecondsToMeetClient(), 342);
  }

  @Test
  public void testGetProgressLeft() {
    // Дано:
    when(timeUtils.currentTimeMillis()).thenReturn(12395182L, 12400182L);
    when(order.getTimeout()).thenReturn(20_000L);

    // Результат:
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