package com.cargopull.executor_driver.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Бизнес сущность двоичного параметра автомобиля.
 */
public class OptionBoolean implements Option<Boolean> {

  private final long id;
  @NonNull
  private final String name;
  @Nullable
  private final String description;
  private final boolean value;

  public OptionBoolean(long id, @NonNull String name, @Nullable String description, boolean value) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.value = value;
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
  public Boolean getValue() {
    return value;
  }

  @Override
  @NonNull
  public OptionBoolean setValue(@NonNull Boolean value) {
    return new OptionBoolean(id, name, description, value);
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
        ", description='" + description + '\'' +
        ", value=" + value +
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

    OptionBoolean that = (OptionBoolean) o;

    if (id != that.id) {
      return false;
    }
    if (value != that.value) {
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
    result = 31 * result + (value ? 1 : 0);
    return result;
  }
}
