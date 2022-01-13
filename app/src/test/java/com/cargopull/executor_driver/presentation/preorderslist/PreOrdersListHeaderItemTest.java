package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import android.content.res.Resources;

import com.cargopull.executor_driver.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListHeaderItemTest {

  private PreOrdersListItem preOrdersListItem;

  @Mock
  private Resources resources;

  @Before
  public void setUp() {
    preOrdersListItem = new PreOrdersListHeaderItem(0);
  }

  @Test
  public void testGetOrder() {
    // Effect:
    assertNull(preOrdersListItem.getOrder());
  }

  @Test
  public void testGetViewType() {
      // Effect:
    assertEquals(preOrdersListItem.getViewType(), PreOrdersListItem.TYPE_HEADER);
  }

  @Test
  public void testGetNextAddressIfAllQueued() {
      // Effect:
    assertEquals(preOrdersListItem.getNextAddress(), "");
  }

  @Test
  public void testGetNextAddressIfAllClosed() {
      // Effect:
    assertEquals(preOrdersListItem.getNextAddress(), "");
  }

  @Test
  public void testGetNextAddressIfSecondActive() {
      // Effect:
    assertEquals(preOrdersListItem.getNextAddress(), "");
  }

  @Test
  public void testGetEstimatedPrice() {
    assertEquals(preOrdersListItem.getEstimatedPrice(resources), "");
  }

  @Test
  public void testGetRouteLength() {
      // Effect:
    assertEquals(preOrdersListItem.getRouteLength(), 0, 0);
  }

  @Test
  public void testGetOccupationTime() {
    assertEquals(preOrdersListItem.getOccupationTime(), "");
  }

  @Test
  public void testGetOccupationDayOfMonthToday() {
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(0);

      // Effect:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()));
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationDayOfMonthTomorrow() {
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(1);

      // Effect:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()));
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationDayOfMonthDayAfterTomorrow() {
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(2);

      // Effect:
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
      // Given:
    when(resources.getString(R.string.today)).thenReturn("today 1");
    preOrdersListItem = new PreOrdersListHeaderItem(0);

      // Effect:
    System.out.println(DateTimeFormat.forPattern("MMMM HH:mm").print(
        DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals("today 1", preOrdersListItem.getOccupationMonth(resources));
  }

  @Test
  public void testGetOccupationMonthTomorrow() {
      // Given:
    when(resources.getString(R.string.tomorrow)).thenReturn("tomorrow 1");
    preOrdersListItem = new PreOrdersListHeaderItem(1);

      // Effect:
    System.out.println(DateTimeFormat.forPattern("MMMM HH:mm").print(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals("tomorrow 1", preOrdersListItem.getOccupationMonth(resources));
  }

  @Test
  public void testGetOccupationMonthDayAfterTomorrow() {
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(2);

      // Effect:
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
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(0);

      // Effect:
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
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(1);

      // Effect:
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
      // Given:
    preOrdersListItem = new PreOrdersListHeaderItem(2);

      // Effect:
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

  @Test
  public void testEquals() {
    assertEquals(preOrdersListItem, preOrdersListItem);
    assertEquals(preOrdersListItem, new PreOrdersListHeaderItem(0));
    assertNotEquals(preOrdersListItem, new PreOrdersListHeaderItem(1));
    assertNotEquals(preOrdersListItem, null);
    assertNotEquals(preOrdersListItem, "");
  }
}