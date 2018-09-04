package com.cargopull.executor_driver.presentation.preorderslist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;

/**
 * Модель для отображения карточки предзаказа в списке предзаказов исполнителя. Тестируемое форматирование.
 */
public class PreOrdersListItem {

  @NonNull
  private Order order;

  PreOrdersListItem(@NonNull Order order) {
    this.order = order;
  }

  @NonNull
  public Order getOrder() {
    return order;
  }

  public String getOccupationTime() {
    long scheduledDate = order.getScheduledStartTime();
    return
        DateTimeFormat.forPattern("HH:mm").print(scheduledDate)
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(scheduledDate + order.getEstimatedTime());
  }

  @NonNull
  public String getNextAddress() {
    RoutePoint routePoint = getFirstActiveRoutePoint();
    return routePoint == null ? "" : routePoint.getAddress().trim();
  }

  public String getRouteLength() {
    return String.format(Locale.getDefault(), "%.2f", order.getEstimatedRouteLength() / 1000d);
  }

  public long getEstimatedPrice() {
    return order.getEstimatedPrice();
  }

  @Nullable
  private RoutePoint getFirstActiveRoutePoint() {
    for (RoutePoint routePoint : order.getRoutePath()) {
      if (routePoint.getRoutePointState() == RoutePointState.ACTIVE) {
        return routePoint;
      }
    }
    return order.getRoutePath().get(0);
  }
}
