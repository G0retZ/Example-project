package com.cargopull.executor_driver.presentation.preorderslist;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import java.text.DecimalFormat;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;

/**
 * Модель для отображения карточки предзаказа в списке предзаказов исполнителя. Тестируемое форматирование.
 */
public class PreOrdersListOrderItem implements PreOrdersListItem {

  @NonNull
  private final Order order;

  PreOrdersListOrderItem(@NonNull Order order) {
    this.order = order;
  }

  @NonNull
  @Override
  public Order getOrder() {
    return order;
  }

  @Override
  @LayoutRes
  public int getLayoutType() {
    return R.layout.fragment_preorders_list_item;
  }

  @Override
  @NonNull
  public String getOccupationDayOfMonth() {
    return "";
  }

  @Override
  @NonNull
  public String getOccupationMonth(@NonNull Resources resources) {
    return "";
  }

  @Override
  @NonNull
  public String getOccupationDayOfWeek() {
    return "";
  }

  @Override
  @NonNull
  public String getOccupationTime() {
    long scheduledDate = order.getScheduledStartTime();
    return
        DateTimeFormat.forPattern("HH:mm").print(scheduledDate)
            + "–"
            + DateTimeFormat.forPattern("HH:mm").print(scheduledDate + order.getEstimatedTime());
  }

  @Override
  @NonNull
  public String getNextAddress() {
    RoutePoint routePoint = getFirstActiveRoutePoint();
    return routePoint == null ? "" : routePoint.getAddress().trim();
  }

  @Override
  @NonNull
  public String getRouteLength() {
    return String.format(Locale.getDefault(), "%.2f", order.getEstimatedRouteLength() / 1000d);
  }

  @Override
  @NonNull
  public String getEstimatedPrice(@NonNull Resources resources) {
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
