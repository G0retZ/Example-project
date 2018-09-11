package com.cargopull.executor_driver.presentation.preorderslist;

import com.cargopull.executor_driver.entity.Order;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.joda.time.DateTime;

/**
 * Маппер списка предзаказов в сортированный список моделей.
 */
public class PreOrdersListItemsMapper implements Function<Set<Order>, List<PreOrdersListItem>> {

  @Inject
  public PreOrdersListItemsMapper() {
  }

  @Override
  public List<PreOrdersListItem> apply(Set<Order> orders) {
    DateTime today = DateTime.now().withTimeAtStartOfDay();
    List<PreOrdersListItem> preOrdersListItems = new ArrayList<>();
    ArrayList<Order> ordersList = new ArrayList<>(orders);
    Collections.sort(ordersList,
        (o1, o2) -> Long.compare(o1.getScheduledStartTime(), o2.getScheduledStartTime()));
    int offset = -1;
    for (Order order : ordersList) {
      DateTime preOrderStartDate = new DateTime(order.getScheduledStartTime())
          .withTimeAtStartOfDay();
      if (preOrderStartDate.compareTo(today.plusDays(offset)) > 0) {
        offset++;
        preOrdersListItems.add(new PreOrdersListHeaderItem(offset));
      }
      preOrdersListItems.add(new PreOrdersListOrderItem(order));
    }
    return preOrdersListItems;
  }
}
