package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность числового параметра автомобиля.
 */
public class VehicleOptionNumeric extends VehicleOption<Integer> {

  private final int minValue;
  private final int maxValue;

  public VehicleOptionNumeric(long id, @NonNull String name, boolean variable,
      @NonNull Integer value, int minValue,
      int maxValue) {
    super(id, name, variable, value);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public int getMinValue() {
    return minValue;
  }

  public int getMaxValue() {
    return maxValue;
  }

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  VehicleOptionNumeric setValue(@NonNull Integer value) {
    return new VehicleOptionNumeric(getId(), getName(), isVariable(), value, minValue, maxValue);
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
