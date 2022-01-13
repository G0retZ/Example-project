package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.res.Resources;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DecimalFormat;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListOrderItemTest {

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
    preOrdersListItem = new PreOrdersListOrderItem(order);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
  }

  @Test
  public void testGetOrder() {
    // Effect:
    assertEquals(preOrdersListItem.getOrder(), order);
  }

  @Test
  public void testGetViewType() {
    // Effect:
    assertEquals(preOrdersListItem.getViewType(), PreOrdersListItem.TYPE_ITEM);
  }

  @Test
  public void testGetNextAddressIfAllQueued() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint.getAddress()).thenReturn("add0");

    // Effect:
    assertEquals(preOrdersListItem.getNextAddress(), "add0");
  }

  @Test
  public void testGetNextAddressIfAllClosed() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint.getAddress()).thenReturn("add0");

    // Effect:
    assertEquals(preOrdersListItem.getNextAddress(), "add0");
  }

  @Test
  public void testGetNextAddressIfSecondActive() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(routePoint1.getAddress()).thenReturn("add1");

    // Effect:
    assertEquals(preOrdersListItem.getNextAddress(), "add1");
  }

  @Test
  public void testGetEstimatedPrice() {
    // Given:
    when(resources.getString(R.string.currency_format)).thenReturn("##,###,### ₽");
    when(order.getEstimatedPrice()).thenReturn(700000L);

    // Effect:
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    assertEquals(preOrdersListItem.getEstimatedPrice(resources), decimalFormat.format(7000));
  }

  @Test
  public void testGetRouteLength() {
    // Given:
    when(order.getEstimatedRouteLength()).thenReturn(33239L);

    // Effect:
    assertEquals(preOrdersListItem.getRouteLength(), 33.239f, 0);
  }

  @Test
  public void testGetOccupationTime() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(22792192L);
    when(order.getEstimatedTime()).thenReturn(3324339L);

    // Effect:
    assertEquals(preOrdersListItem.getOccupationTime(),
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(22792192L).withZone(DateTimeZone.forOffsetHours(3)))
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(26116531L).withZone(DateTimeZone.forOffsetHours(3)))
    );
  }

  @Test
  public void testGetOccupationDayOfMonth() {
    // Effect:
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationMonth() {
    // Effect:
    assertEquals("", preOrdersListItem.getOccupationMonth(resources));
  }

  @Test
  public void testGetOccupationDayOfWeekToday() {
    // Effect:
    assertEquals("", preOrdersListItem.getOccupationDayOfWeek());
  }
}