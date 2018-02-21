package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность обобщенного параметра автомобиля. Содержит в себе ID, имя и значение.
 *
 * @param <V> тип значения
 */
public class VehicleOption<V> {

  private final long id;
  @NonNull
  private final String name;
  private final boolean variable;
  @NonNull
  private final V value;
  @NonNull
  private final V minValue;
  @NonNull
  private final V maxValue;

  VehicleOption(long id, @NonNull String name, boolean variable, @NonNull V value,
      @NonNull V minValue, @NonNull V maxValue) {
    this.id = id;
    this.name = name;
    this.variable = variable;
    this.value = value;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public boolean isVariable() {
    return variable;
  }

  @NonNull
  public V getValue() {
    return value;
  }

  @NonNull
  public V getMinValue() {
    return minValue;
  }

  @NonNull
  public V getMaxValue() {
    return maxValue;
  }

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  @NonNull
  public VehicleOption<V> setValue(@NonNull V value) {
    return new VehicleOption<>(id, name, variable, value, minValue, maxValue);
  }

  @Override
  public String toString() {
    return "VehicleOption{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", variable=" + variable +
        ", value=" + value +
        ", minValue=" + minValue +
        ", maxValue=" + maxValue +
        '}';
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VehicleOption<?> that = (VehicleOption<?>) o;

    if (id != that.id) {
      return false;
    }
    if (variable != that.variable) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    if (!value.equals(that.value)) {
      return false;
    }
    if (!minValue.equals(that.minValue)) {
      return false;
    }
    return maxValue.equals(that.maxValue);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + (variable ? 1 : 0);
    result = 31 * result + value.hashCode();
    result = 31 * result + minValue.hashCode();
    result = 31 * result + maxValue.hashCode();
    return result;
  }
}
