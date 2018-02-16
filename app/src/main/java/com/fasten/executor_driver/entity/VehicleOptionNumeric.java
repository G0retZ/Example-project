package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность числового параметра автомобиля.
 */
public class VehicleOptionNumeric extends VehicleOption<Integer> {

  public VehicleOptionNumeric(long id, @NonNull String name, boolean variable,
      @NonNull Integer value, int minValue, int maxValue) {
    super(id, name, variable, value, minValue, maxValue);
  }

  @Override
  @NonNull
  public VehicleOptionNumeric setValue(@NonNull Integer value) {
    return new VehicleOptionNumeric(getId(), getName(), isVariable(), value, getMinValue(), getMaxValue());
  }

  @Override
  public String toString() {
    return "VehicleOptionNumeric{" + super.toString() + "}";
  }
}
