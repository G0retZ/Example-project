package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
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
    // Результат:
    assertNull(preOrdersListItem.getOrder());
  }

  @Test
  public void testGetLayoutType() {
    // Результат:
    assertEquals(preOrdersListItem.getLayoutType(), R.layout.fragment_preorders_list_header);
  }

  @Test
  public void testGetNextAddressIfAllQueued() {
    // Результат:
    assertEquals(preOrdersListItem.getNextAddress(), "");
  }

  @Test
  public void testGetNextAddressIfAllClosed() {
    // Результат:
    assertEquals(preOrdersListItem.getNextAddress(), "");
  }

  @Test
  public void testGetNextAddressIfSecondActive() {
    // Результат:
    assertEquals(preOrdersListItem.getNextAddress(), "");
  }

  @Test
  public void testGetEstimatedPrice() {
    assertEquals(preOrdersListItem.getEstimatedPrice(resources), "");
  }

  @Test
  public void testGetRouteLength() {
    // Результат:
    assertEquals(preOrdersListItem.getRouteLength(), "");
  }

  @Test
  public void testGetOccupationTime() {
    assertEquals(preOrdersListItem.getOccupationTime(), "");
  }

  @Test
  public void testGetOccupationDayOfMonthToday() {
    // Дано:
    preOrdersListItem = new PreOrdersListHeaderItem(0);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(1).withMillisOfDay(0).minusMillis(1).getMillis()));
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationDayOfMonthTomorrow() {
    // Дано:
    preOrdersListItem = new PreOrdersListHeaderItem(1);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("d HH:mm")
        .print(DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis()));
    assertEquals("", preOrdersListItem.getOccupationDayOfMonth());
  }

  @Test
  public void testGetOccupationDayOfMonthDayAfterTomorrow() {
    // Дано:
    preOrdersListItem = new PreOrdersListHeaderItem(2);

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
    preOrdersListItem = new PreOrdersListHeaderItem(0);

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
    preOrdersListItem = new PreOrdersListHeaderItem(1);

    // Результат:
    System.out.println(DateTimeFormat.forPattern("MMMM HH:mm").print(
        DateTime.now().plusDays(2).withMillisOfDay(0).minusMillis(1).getMillis())
    );
    assertEquals("tomorrow 1", preOrdersListItem.getOccupationMonth(resources));
  }

  @Test
  public void testGetOccupationMonthDayAfterTomorrow() {
    // Дано:
    preOrdersListItem = new PreOrdersListHeaderItem(2);

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
    preOrdersListItem = new PreOrdersListHeaderItem(0);

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
    preOrdersListItem = new PreOrdersListHeaderItem(1);

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
    preOrdersListItem = new PreOrdersListHeaderItem(2);

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