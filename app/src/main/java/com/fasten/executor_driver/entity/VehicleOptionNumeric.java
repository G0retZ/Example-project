package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность числового параметра автомобиля.
 */
class VehicleOptionNumeric extends VehicleOption<Integer>{

  private final int minValue;
  private final int maxValue;

  VehicleOptionNumeric(int id, @NonNull String name, @NonNull Integer value, int minValue,
      int maxValue) {
    super(id, name, value);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  int getMinValue() {
    return minValue;
  }

  int getMaxValue() {
    return maxValue;
  }

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  VehicleOptionNumeric setValue(@NonNull Integer value) {
    return new VehicleOptionNumeric(getId(), getName(), value, minValue, maxValue);
  }

  @Override
  public String toString() {
    return "VehicleOptionNumeric{" +
        "minValue=" + minValue +
        ", maxValue=" + maxValue +
        super.toString() +
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
    if (!super.equals(o)) {
      return false;
    }

    VehicleOptionNumeric that = (VehicleOptionNumeric) o;

    return minValue == that.minValue && maxValue == that.maxValue;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + minValue;
    result = 31 * result + maxValue;
    return result;
  }
}
