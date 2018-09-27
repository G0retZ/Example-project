package com.cargopull.executor_driver.presentation.preorderslist;

import com.cargopull.executor_driver.entity.Order;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 * Маппер списка предзаказов в сортированный список моделей.
 */
public class PreOrdersListItemsMapper implements Function<Set<Order>, List<PreOrdersListItem>> {

  @Inject
  public PreOrdersListItemsMapper() {
  }

  @Override
  public List<PreOrdersListItem> apply(Set<Order> orders) {
    if (orders.isEmpty()) {
      return new ArrayList<>();
    }
    List<PreOrdersListItem> preOrdersListItems = new ArrayList<>();
    ArrayList<Order> ordersList = new ArrayList<>(orders);
    Collections.sort(ordersList,
        (o1, o2) -> Long.compare(o1.getScheduledStartTime(), o2.getScheduledStartTime()));
    // Устанавливаем первый заголовок
    preOrdersListItems.add(new PreOrdersListHeaderItem(
        Days.daysBetween(
            LocalDate.now(),
            new LocalDate(ordersList.get(0).getScheduledStartTime())
        ).getDays()
    ));
    for (int i = 0; i < ordersList.size(); i++) {
      // Если это второй и более элемент
      if (i > 0) {
        LocalDate preOrderStartDate0 = new LocalDate(ordersList.get(i - 1).getScheduledStartTime());
        LocalDate preOrderStartDate1 = new LocalDate(ordersList.get(i).getScheduledStartTime());
        // Количество дней между предыдущим предзаказом и текущим предзаказом
        int difference = Days.daysBetween(preOrderStartDate0, preOrderStartDate1).getDays();
        // Если разница больше 0
        if (difference > 0) {
          // Добавляем заголовок со сдвигом относительно сейчас
          preOrdersListItems.add(new PreOrdersListHeaderItem(
              Days.daysBetween(LocalDate.now(), preOrderStartDate1).getDays()
          ));
        }
      }
      // Добавляем элемент в список
      preOrdersListItems.add(new PreOrdersListOrderItem(ordersList.get(i)));
    }
    return preOrdersListItems;
  }
}
