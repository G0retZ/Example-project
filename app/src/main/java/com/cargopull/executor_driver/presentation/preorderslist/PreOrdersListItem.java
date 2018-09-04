package com.cargopull.executor_driver.presentation.preorderslist;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;

interface PreOrdersListItem {

  @Nullable
  Order getOrder();

  @LayoutRes
  int getLayoutType();

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

  @NonNull
  String getRouteLength();

  @NonNull
  String getEstimatedPrice(@NonNull Resources resources);
}
