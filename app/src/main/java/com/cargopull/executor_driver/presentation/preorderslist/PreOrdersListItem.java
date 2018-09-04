package com.cargopull.executor_driver.presentation.preorderslist;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import java.text.DecimalFormat;
import java.util.Locale;
import org.joda.time.DateTime;
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

  public String getOccupationDayOfMonth() {
    DateTime dateTime = new DateTime(order.getScheduledStartTime());
    if (dateTime.isBefore(DateTime.now().plusDays(2).withMillisOfDay(0))) {
      return "";
    }
    return DateTimeFormat.forPattern("d").print(dateTime);
  }

  public String getOccupationMonth(Resources resources) {
    DateTime dateTime = new DateTime(order.getScheduledStartTime());
    if (dateTime.isBefore(DateTime.now().plusDays(1).withMillisOfDay(0))) {
      return resources.getString(R.string.today);
    } else if (dateTime.isBefore(DateTime.now().plusDays(2).withMillisOfDay(0))) {
      return resources.getString(R.string.tomorrow);
    }
    return DateTimeFormat.forPattern("MMMM").print(dateTime);
  }

  public String getOccupationDayOfWeek() {
    return DateTimeFormat.forPattern("EEEE").print(order.getScheduledStartTime());
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

  public String getEstimatedPrice(Resources resources) {
    long currentCost = order.getEstimatedPrice();
    if (!resources.getBoolean(R.bool.show_cents)) {
      currentCost = Math.round(currentCost / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(resources.getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    return decimalFormat.format(currentCost);
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
