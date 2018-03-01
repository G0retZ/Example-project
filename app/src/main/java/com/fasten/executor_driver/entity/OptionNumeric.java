package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность числового параметра автомобиля.
 */
public class OptionNumeric implements Option<Integer> {

  private final long id;
  @NonNull
  private final String name;
  @NonNull
  private final String description;
  private final boolean variable;
  @NonNull
  private final Integer value;
  @NonNull
  private final Integer minValue;
  @NonNull
  private final Integer maxValue;

  public OptionNumeric(long id, @NonNull String name, @NonNull String description,
      boolean variable, @NonNull Integer value,
      int minValue, int maxValue) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.variable = variable;
    this.value = value;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  @NonNull
  public OptionNumeric setValue(@NonNull Integer value) {
    return new OptionNumeric(id, name, description, variable, value, minValue, maxValue);
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

  @NonNull
  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isVariable() {
    return variable;
  }

  @NonNull
  @Override
  public Integer getValue() {
    return value;
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

    OptionNumeric that = (OptionNumeric) o;

    if (id != that.id) {
      return false;
    }
    if (variable != that.variable) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    if (!description.equals(that.description)) {
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
    result = 31 * result + description.hashCode();
    result = 31 * result + (variable ? 1 : 0);
    result = 31 * result + value.hashCode();
    result = 31 * result + minValue.hashCode();
    result = 31 * result + maxValue.hashCode();
    return result;
  }
}
