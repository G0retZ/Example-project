package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;

/**
 * Модель для отображения опции ТС в списке опций ТС исполнителя. Тестируемое форматирование.
 */
public class VehicleOptionsListItem<V> {

  @LayoutRes
  private static final int TYPE_SWITCH = R.layout.fragment_vehicle_options_list_item_boolean;
  @LayoutRes
  private static final int TYPE_SLIDER = R.layout.fragment_vehicle_options_list_item_numeric;

  @NonNull
  private Option<V> option;

  VehicleOptionsListItem(@NonNull Option<V> option) {
    this.option = option;
  }

  @NonNull
  public Option<V> getOption() {
    return option;
  }

  @NonNull
  public String getName() {
    return option.getName();
  }

  @Nullable
  public String getDescription() {
    return option.getDescription();
  }

  @LayoutRes
  public int getLayoutType() {
    if (option instanceof OptionNumeric) {
      return TYPE_SLIDER;
    } else if (option instanceof OptionBoolean) {
      return TYPE_SWITCH;
    }
    return TYPE_SWITCH;
  }

  @NonNull
  public V getValue() {
    return option.getValue();
  }

  public <VA extends V> void setValue(@NonNull VA value) {
    option = option.setValue(value);
  }

  @NonNull
  public V getMinValue() {
    return option.getMinValue();
  }

  @NonNull
  public V getMaxValue() {
    return option.getMaxValue();
  }

  @Override
  public String toString() {
    return "ChooseVehicleListItem{" +
        "option=" + option +
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

    return option.equals(that.option);
  }

  @Override
  public int hashCode() {
    return option.hashCode();
  }
}
