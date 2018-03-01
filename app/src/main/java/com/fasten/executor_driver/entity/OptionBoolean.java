package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность двоичного параметра автомобиля.
 */
public class OptionBoolean implements Option<Boolean> {

  private final long id;
  @NonNull
  private final String name;
  private final boolean variable;
  @NonNull
  private final Boolean value;

  public OptionBoolean(long id, @NonNull String name, boolean variable, @NonNull Boolean value) {
    this.id = id;
    this.name = name;
    this.variable = variable;
    this.value = value;
  }

  @Override
  @NonNull
  public OptionBoolean setValue(@NonNull Boolean value) {
    return new OptionBoolean(getId(), getName(), isVariable(), value);
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

  @Override
  public boolean isVariable() {
    return variable;
  }

  @NonNull
  @Override
  public Boolean getValue() {
    return value;
  }

  @NonNull
  @Override
  public Boolean getMinValue() {
    return false;
  }

  @NonNull
  @Override
  public Boolean getMaxValue() {
    return true;
  }

  @Override
  public String toString() {
    return "OptionBoolean{" +
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

    OptionBoolean that = (OptionBoolean) o;

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
