package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.res.Resources;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListItemTest {

  private PreOrdersListItem preOrdersListItem;

  @Mock
  private Order order;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;
  @Mock
  private Resources resources;

  @Before
  public void setUp() {
    preOrdersListItem = new PreOrdersListItem(order);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
  }

  @Test
  public void testGetNextAddressIfAllQueued() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint.getAddress()).thenReturn("add0");

    // Результат:
    assertEquals(preOrdersListItem.getNextAddress(), "add0");
  }

  @Test
  public void testGetNextAddressIfAllClosed() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint.getAddress()).thenReturn("add0");

    // Результат:
    assertEquals(preOrdersListItem.getNextAddress(), "add0");
  }

  @Test
  public void testGetNextAddressIfSecondActive() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(routePoint1.getAddress()).thenReturn("add1");

    // Результат:
    assertEquals(preOrdersListItem.getNextAddress(), "add1");
  }

  @Test
  public void testGetEstimatedPrice() {
    // Дано:
    when(resources.getString(R.string.currency_format)).thenReturn("##,###,### ¤");
    when(order.getEstimatedPrice()).thenReturn(700000L);

    // Результат:
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ¤");
    decimalFormat.setMaximumFractionDigits(0);
    assertEquals(preOrdersListItem.getEstimatedPrice(resources), decimalFormat.format(7000));
  }

  @Test
  public void testGetRouteLength() {
    // Дано:
    when(order.getEstimatedRouteLength()).thenReturn(33239L);

    // Результат:
    assertEquals(preOrdersListItem.getRouteLength(),
        String.format(Locale.getDefault(), "%.2f", 33239 / 1000d));
  }

  @Test
  public void testGetOccupationTime() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(22792192L);
    when(order.getEstimatedTime()).thenReturn(3324339L);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("HH:mm").print(22792192L)
        + "–"
        + DateTimeFormat.forPattern("HH:mm").print(26116531L));
    assertEquals(preOrdersListItem.getOccupationTime(),
        DateTimeFormat.forPattern("HH:mm").print(22792192L)
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(26116531L));
  }

  @Test
  public void testGetOccupationDayOfMonthToday() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()));
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationDayOfMonthTomorrow() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()));
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationDayOfMonthDayAfterTomorrow() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()));
    assertEquals(
        DateTimeFormat.forPattern("d").print(
            DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()
        ),
        preOrdersListItem.getOccupationDayOfMonth()
    );
  }

  @Test
  public void testGetOccupationMonthToday() {
    // Дано:
    when(resources.getString(R.string.today)).thenReturn("today 1");
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("MMMM HH:mm").print(
        DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals("today 1", preOrdersListItem.getOccupationMonth(resources));
  }

  @Test
  public void testGetOccupationMonthTomorrow() {
    // Дано:
    when(resources.getString(R.string.tomorrow)).thenReturn("tomorrow 1");
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("MMMM HH:mm").print(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals("tomorrow 1", preOrdersListItem.getOccupationMonth(resources));
  }

  @Test
  public void testGetOccupationMonthDayAfterTomorrow() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("MMMM HH:mm").print(
        DateTime.now().plusDays(2).withMillisOfDay(0).getMillis())
    );
    assertEquals(
        DateTimeFormat.forPattern("MMMM").print(
            DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()
        ),
        preOrdersListItem.getOccupationMonth(resources)
    );
  }

  @Test
  public void testGetOccupationDayOfWeekToday() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("EEEE HH:mm").print(
        DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals(
        DateTimeFormat.forPattern("EEEE").print(
            DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()
        ),
        preOrdersListItem.getOccupationDayOfWeek()
    );
  }

  @Test
  public void testGetOccupationDayOfWeekTomorrow() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("EEEE HH:mm").print(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals(
        DateTimeFormat.forPattern("EEEE").print(
            DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()
        ),
        preOrdersListItem.getOccupationDayOfWeek()
    );
  }

  @Test
  public void testGetOccupationDayOfWeekDayAfterTomorrow() {
    // Дано:
    when(order.getScheduledStartTime()).thenReturn(
        DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()
    );

    // Результат:
    System.out.println(DateTimeFormat.forPattern("EEEE HH:mm").print(
        DateTime.now().plusDays(2).withMillisOfDay(0).getMillis())
    );
    assertEquals(
        DateTimeFormat.forPattern("EEEE").print(
            DateTime.now().plusDays(2).withMillisOfDay(0).getMillis()
        ),
        preOrdersListItem.getOccupationDayOfWeek()
    );
  }
}