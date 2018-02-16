package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;

/**
 * Модель для отображения опции ТС в списке опций ТС исполнителя. Тестируемое форматирование.
 */
class VehicleOptionsListItem<V> {

  @LayoutRes
  private static final int TYPE_SWITCH = R.layout.fragment_vehicle_options_list_item_boolean;
  @LayoutRes
  private static final int TYPE_SLIDER = R.layout.fragment_vehicle_options_list_item_numeric;

  @NonNull
  private VehicleOption<V> vehicleOption;

  VehicleOptionsListItem(@NonNull VehicleOption<V> vehicleOption) {
    this.vehicleOption = vehicleOption;
  }

  @NonNull
  VehicleOption<V> getVehicleOption() {
    return vehicleOption;
  }

  @NonNull
  String getName() {
    return vehicleOption.getName();
  }

  @LayoutRes
  int getLayoutType() {
    if (vehicleOption instanceof VehicleOptionNumeric) {
      return TYPE_SLIDER;
    } else if (vehicleOption instanceof VehicleOptionBoolean) {
      return TYPE_SWITCH;
    }
    return TYPE_SWITCH;
  }

  @NonNull
  V getValue() {
    return vehicleOption.getValue();
  }

  @NonNull
  V getMinValue() {
    return vehicleOption.getMinValue();
  }

  @NonNull
  V getMaxValue() {
    return vehicleOption.getMaxValue();
  }

  <VA extends V> void setValue(@NonNull VA value) {
    vehicleOption = vehicleOption.setValue(value);
  }

  @Override
  public String toString() {
    return "ChooseVehicleListItem{" +
        "vehicleOption=" + vehicleOption +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VehicleOptionsListItem that = (VehicleOptionsListItem) o;

    return vehicleOption.equals(that.vehicleOption);
  }

  @Override
  public int hashCode() {
    return vehicleOption.hashCode();
  }
}
