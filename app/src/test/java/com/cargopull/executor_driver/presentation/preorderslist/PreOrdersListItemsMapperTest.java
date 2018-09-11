package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.Order;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListItemsMapperTest {

  private PreOrdersListItemsMapper preOrdersListItemsMapper;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;

  @Before
  public void setUp() {
    preOrdersListItemsMapper = new PreOrdersListItemsMapper();
  }

  @Test
  public void testSortWithOneHeader() {
    // Дано:
    when(order.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(3).getMillis());
    when(order1.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(11).getMillis());
    when(order2.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(7).getMillis());
    when(order3.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(15).getMillis());

    // Действие:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Результат:
    assertEquals(preOrdersListItems.size(), 5);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3).getOrder(), order1);
    assertEquals(preOrdersListItems.get(4).getOrder(), order3);
  }

  @Test
  public void testSortWithTwoHeaders() {
    // Дано:
    when(order.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(3).getMillis());
    when(order1.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(19).getMillis());
    when(order2.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(11).getMillis());
    when(order3.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(27).getMillis());

    // Действие:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Результат:
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
    // Дано:
    when(order.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(3).getMillis());
    when(order1.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(35).getMillis());
    when(order2.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(19).getMillis());
    when(order3.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusHours(51).getMillis());

    // Действие:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Результат:
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
    // Дано:
    when(order.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(3).getMillis());
    when(order1.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(35).getMillis());
    when(order2.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(19).getMillis());
    when(order3.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(51).getMillis());

    // Действие:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Результат:
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
    // Дано:
    when(order.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().minusDays(1).plusHours(3).getMillis());
    when(order1.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().minusDays(1).plusHours(35).getMillis());
    when(order2.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().minusDays(1).plusHours(19).getMillis());
    when(order3.getScheduledStartTime())
        .thenReturn(DateTime.now().withTimeAtStartOfDay().minusDays(1).plusHours(51).getMillis());

    // Действие:
    List<PreOrdersListItem> preOrdersListItems =
        preOrdersListItemsMapper.apply(new HashSet<>(Arrays.asList(order, order1, order2, order3)));

    // Результат:
    assertEquals(preOrdersListItems.size(), 7);
    assertEquals(preOrdersListItems.get(0), new PreOrdersListHeaderItem(-1));
    assertEquals(preOrdersListItems.get(1).getOrder(), order);
    assertEquals(preOrdersListItems.get(2).getOrder(), order2);
    assertEquals(preOrdersListItems.get(3), new PreOrdersListHeaderItem(0));
    assertEquals(preOrdersListItems.get(4).getOrder(), order1);
    assertEquals(preOrdersListItems.get(5), new PreOrdersListHeaderItem(1));
    assertEquals(preOrdersListItems.get(6).getOrder(), order3);
  }
}