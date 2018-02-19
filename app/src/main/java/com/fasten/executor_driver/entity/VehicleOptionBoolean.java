package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность двоичного параметра автомобиля.
 */
public class VehicleOptionBoolean extends VehicleOption<Boolean> {

  public VehicleOptionBoolean(long id, @NonNull String name, boolean variable,
      @NonNull Boolean value) {
    super(id, name, variable, value, false, true);
  }

  @Override
  @NonNull
  public VehicleOptionBoolean setValue(@NonNull Boolean value) {
    return new VehicleOptionBoolean(getId(), getName(), isVariable(), value);
  }

  @Override
  public String toString() {
    return "VehicleOptionBoolean{" + super.toString() + "}";
  }
}
