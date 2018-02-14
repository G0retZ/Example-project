package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность обобщенного параметра автомобиля. Содержит в себе ID, имя и значение.
 *
 * @param <V> тип значения.
 */
class VehicleOption<V> {

  private final long id;
  @NonNull
  private final String name;
  private final boolean variable;
  @NonNull
  private final V value;

  VehicleOption(long id, @NonNull String name, boolean variable, @NonNull V value) {
    this.id = id;
    this.name = name;
    this.variable = variable;
    this.value = value;
  }

  long getId() {
    return id;
  }

  @NonNull
  String getName() {
    return name;
  }

  boolean isVariable() {
    return variable;
  }

  @NonNull
  V getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "VehicleOption{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", variable=" + variable +
        ", value=" + value +
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
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + (variable ? 1 : 0);
    result = 31 * result + value.hashCode();
    return result;
  }
}
