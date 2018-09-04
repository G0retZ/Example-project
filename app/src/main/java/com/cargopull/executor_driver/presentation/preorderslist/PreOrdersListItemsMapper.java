package com.cargopull.executor_driver.presentation.preorderslist;

import com.cargopull.executor_driver.entity.Order;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.DateTime;

/**
 * Маппер списка предзаказов в сортированный список моделей.
 */
class PreOrdersListItemsMapper implements Function<List<Order>, List<PreOrdersListItem>> {

  @Inject
  PreOrdersListItemsMapper() {
  }

  @Override
  public List<PreOrdersListItem> apply(List<Order> orders) {
    List<PreOrdersListItem> preOrdersListItems = new ArrayList<>();
    Collections.sort(orders,
        (o1, o2) -> Long.compare(o1.getScheduledStartTime(), o2.getScheduledStartTime()));
    int offset = -1;
    for (Order order : orders) {
      DateTime preOrderStartDate = new DateTime(order.getScheduledStartTime());
      if (preOrderStartDate.withMillisOfDay(0).compareTo(DateTime.now().plusDays(offset)) > 0) {
        offset++;
        preOrdersListItems.add(new PreOrdersListHeaderItem(offset));
      }
      preOrdersListItems.add(new PreOrdersListOrderItem(order));
    }
    return preOrdersListItems;
  }
}
