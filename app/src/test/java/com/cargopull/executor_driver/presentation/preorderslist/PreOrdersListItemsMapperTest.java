package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.Order;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RunWith(Parameterized.class)
public class PreOrdersListItemsMapperTest {

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  private PreOrdersListItemsMapper preOrdersListItemsMapper;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;
  private DateTime today;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public PreOrdersListItemsMapperTest(Integer offset) {
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(offset));
  }

  @Parameterized.Parameters
  public static Collection primeNumbers() {
    ArrayList<Integer> offsets = new ArrayList<>();
    for (int i = -15; i < 16; i++) {
      offsets.add(i);
    }
    return offsets;
  }

  @Before
  public void setUp() {
    preOrdersListItemsMapper = new PreOrdersListItemsMapper();
    today = DateTime.now(DateTimeZone.forOffsetHours(3)).withTimeAtStartOfDay();
  }

  @Test
  public void testSortWithEmptySet() {
    // Action:
    List<PreOrdersListItem> preOrdersListItems = preOrdersListItemsMapper.apply(new HashSet<>());

    // Effect:
    assertEquals(preOrdersListItems.size(), 0);
  }

  @Test
  public void testSortWithOneHeader() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(today.plusHours(3).getMillis());
    when(order1.getScheduledStartTime()).thenReturn(today.plusHours(11).getMillis());
    when(order2.getScheduledStartTime()).thenReturn(today.plusHours(7).getMillis());
    when(order3.getScheduledStartTime()).thenReturn(today.plusHours(15).getMillis());

    // Action:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Effect:
    assertEquals(preOrdersListItems.size(), 5);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3).getOrder(), order1);
    assertEquals(preOrdersListItems.get(4).getOrder(), order3);
  }

  @Test
  public void testSortWithTwoHeaders() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(today.plusHours(3).getMillis());
    when(order1.getScheduledStartTime()).thenReturn(today.plusHours(19).getMillis());
    when(order2.getScheduledStartTime()).thenReturn(today.plusHours(11).getMillis());
    when(order3.getScheduledStartTime()).thenReturn(today.plusHours(27).getMillis());

    // Action:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Effect:
    assertEquals(preOrdersListItems.size(), 6);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3).getOrder(), order1);
    assertEquals(preOrdersListItems.get(4), new PreOrdersListHeaderItem(1));
    assertEquals(preOrdersListItems.get(5).getOrder(), order3);
  }

  @Test
  public void testSortWithThreeHeaders() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(today.plusHours(3).getMillis());
    when(order1.getScheduledStartTime()).thenReturn(today.plusDays(1).plusHours(11).getMillis());
    when(order2.getScheduledStartTime()).thenReturn(today.plusHours(19).getMillis());
    when(order3.getScheduledStartTime()).thenReturn(today.plusDays(2).plusHours(3).getMillis());

    // Action:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Effect:
    assertEquals(preOrdersListItems.size(), 7);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3), new PreOrdersListHeaderItem(1));
    assertEquals(preOrdersListItems.get(4).getOrder(), order1);
    assertEquals(preOrdersListItems.get(5), new PreOrdersListHeaderItem(2));
    assertEquals(preOrdersListItems.get(6).getOrder(), order3);
  }

  @Test
  public void testSortWithThreeHeadersStartingFromTheDayAfterTomorrow() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(today.plusDays(1).plusHours(3).getMillis());
    when(order1.getScheduledStartTime()).thenReturn(today.plusDays(2).plusHours(11).getMillis());
    when(order2.getScheduledStartTime()).thenReturn(today.plusDays(1).plusHours(19).getMillis());
    when(order3.getScheduledStartTime()).thenReturn(today.plusDays(3).plusHours(3).getMillis());

    // Action:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Effect:
    assertEquals(preOrdersListItems.size(), 7);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(1));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3), new PreOrdersListHeaderItem(2));
    assertEquals(preOrdersListItems.get(4).getOrder(), order1);
    assertEquals(preOrdersListItems.get(5), new PreOrdersListHeaderItem(3));
    assertEquals(preOrdersListItems.get(6).getOrder(), order3);
  }

  @Test
  public void testSortWithThreeHeadersStartingFromYesterday() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(today.minusDays(1).plusHours(3).getMillis());
    when(order1.getScheduledStartTime()).thenReturn(today.plusHours(11).getMillis());
    when(order2.getScheduledStartTime()).thenReturn(today.minusDays(1).plusHours(19).getMillis());
    when(order3.getScheduledStartTime()).thenReturn(today.plusDays(1).plusHours(3).getMillis());

    // Action:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Effect:
    assertEquals(preOrdersListItems.size(), 7);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(-1));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(4).getOrder(), order1);
    assertEquals(preOrdersListItems.get(5), new PreOrdersListHeaderItem(1));
    assertEquals(preOrdersListItems.get(6).getOrder(), order3);
  }

  @Test
  public void testSortWithThreeHeadersWithGaps() {
    // Given:
    when(order.getScheduledStartTime()).thenReturn(today.minusDays(1).plusHours(3).getMillis());
    when(order1.getScheduledStartTime()).thenReturn(today.plusDays(2).plusHours(11).getMillis());
    when(order2.getScheduledStartTime()).thenReturn(today.plusHours(19).getMillis());
    when(order3.getScheduledStartTime()).thenReturn(today.plusDays(4).plusHours(3).getMillis());

    // Action:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Effect:
    assertEquals(preOrdersListItems.size(), 8);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(-1));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(3).getOrder(), order2);
    assertEquals(preOrdersListItems.get(4), new PreOrdersListHeaderItem(2));
    assertEquals(preOrdersListItems.get(5).getOrder(), order1);
    assertEquals(preOrdersListItems.get(6), new PreOrdersListHeaderItem(4));
    assertEquals(preOrdersListItems.get(7).getOrder(), order3);
  }
}