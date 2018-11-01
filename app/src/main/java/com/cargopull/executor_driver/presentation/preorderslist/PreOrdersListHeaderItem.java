package com.cargopull.executor_driver.presentation.preorderslist;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
  public int getViewType() {
    return TYPE_HEADER;
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

  @Override
  public float getRouteLength() {
    return 0;
  }

  @NonNull
  @Override
  public String getEstimatedPrice(@NonNull Resources resources) {
    return "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PreOrdersListHeaderItem that = (PreOrdersListHeaderItem) o;

    return offset == that.offset;
  }

  @Override
  public int hashCode() {
    return offset;
  }
}
