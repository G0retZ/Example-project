package com.cargopull.executor_driver.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Бизнес сущность числового параметра автомобиля.
 */
public class OptionNumeric implements Option<Integer> {

  private final long id;
  @NonNull
  private final String name;
  @Nullable
  private final String description;
  private final int value;
  private final int minValue;
  private final int maxValue;

  public OptionNumeric(long id, @NonNull String name, @Nullable String description, int value,
      int minValue, int maxValue) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.value = value;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public long getId() {
    return id;
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @Nullable
  @Override
  public String getDescription() {
    return description;
  }

  @NonNull
  @Override
  public Integer getValue() {
    return value;
  }

  @Override
  @NonNull
  public OptionNumeric setValue(@NonNull Integer value) {
    return new OptionNumeric(id, name, description, value, minValue, maxValue);
  }

  @NonNull
  @Override
  public Integer getMinValue() {
    return minValue;
  }

  @NonNull
  @Override
  public Integer getMaxValue() {
    return maxValue;
  }

  @Override
  public String toString() {
    return "OptionNumeric{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", value=" + value +
        ", minValue=" + minValue +
        ", maxValue=" + maxValue +
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

    OptionNumeric that = (OptionNumeric) o;

    if (id != that.id) {
      return false;
    }
    if (value != that.value) {
      return false;
    }
    if (minValue != that.minValue) {
      return false;
    }
    if (maxValue != that.maxValue) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    return description != null ? description.equals(that.description) : that.description == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + value;
    result = 31 * result + minValue;
    result = 31 * result + maxValue;
    return result;
  }
}
