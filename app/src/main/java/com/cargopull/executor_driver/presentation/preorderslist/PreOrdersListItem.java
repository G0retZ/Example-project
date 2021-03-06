package com.cargopull.executor_driver.presentation.preorderslist;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;

/**
 * Модель элемента списка предзаказов исполнителя.
 */
public interface PreOrdersListItem {

  int TYPE_HEADER = 0;
  int TYPE_ITEM = 1;

  @Nullable
  Order getOrder();

  int getViewType();

  @NonNull
  String getOccupationDayOfMonth();

  @NonNull
  String getOccupationMonth(@NonNull Resources resources);

  @NonNull
  String getOccupationDayOfWeek();

  @NonNull
  String getOccupationTime();

  @NonNull
  String getNextAddress();

  float getRouteLength();

  @NonNull
  String getEstimatedPrice(@NonNull Resources resources);
}
