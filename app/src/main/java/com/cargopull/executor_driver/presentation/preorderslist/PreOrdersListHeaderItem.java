package com.cargopull.executor_driver.presentation.preorderslist;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Order;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Модель для отображения заголовка в списке предзаказов исполнителя. Тестируемое форматирование.
 */
class PreOrdersListHeaderItem implements PreOrdersListItem {

  private final int offset;

  PreOrdersListHeaderItem(int offset) {
    this.offset = offset;
  }

  @Nullable
  @Override
  public Order getOrder() {
    return null;
  }

  @Override
  public int getLayoutType() {
    return R.layout.fragment_preorders_list_header;
  }

  @NonNull
  @Override
  public String getOccupationDayOfMonth() {
    if (offset < 2) {
      return "";
    }
    return DateTimeFormat.forPattern("d").print(DateTime.now().plusDays(offset).withMillisOfDay(0));
  }

  @NonNull
  @Override
  public String getOccupationMonth(@NonNull Resources resources) {
    if (offset < 1) {
      return resources.getString(R.string.today);
    } else if (offset < 2) {
      return resources.getString(R.string.tomorrow);
    }
    return DateTimeFormat.forPattern("MMMM")
        .print(DateTime.now().plusDays(offset).withMillisOfDay(0));
  }

  @NonNull
  @Override
  public String getOccupationDayOfWeek() {
    return DateTimeFormat.forPattern("EEEE")
        .print(DateTime.now().plusDays(offset).withMillisOfDay(0));
  }

  @NonNull
  @Override
  public String getOccupationTime() {
    return "";
  }

  @NonNull
  @Override
  public String getNextAddress() {
    return "";
  }

  @NonNull
  @Override
  public String getRouteLength() {
    return "";
  }

  @NonNull
  @Override
  public String getEstimatedPrice(@NonNull Resources resources) {
    return "";
  }
}
