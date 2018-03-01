package com.fasten.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные о параметре ТС.
 */
public class ApiOptionItem {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("name")
  private String name;
  @SuppressWarnings("CanBeFinal")
  @Nullable
  @SerializedName("description")
  private String description;
  @SerializedName("numeric")
  private boolean numeric;
  @SerializedName("dynamic")
  private boolean dynamic;
  @Nullable
  @SerializedName("value")
  private String value;
  @Nullable
  @SerializedName("min")
  private Integer minValue;
  @Nullable
  @SerializedName("max")
  private Integer maxValue;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOptionItem() {
    description = "Детальное описание";
  }

  public ApiOptionItem(long id, @Nullable String name, @Nullable String description,
      boolean numeric, boolean dynamic, @Nullable String value, @Nullable Integer minValue,
      @Nullable Integer maxValue) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.numeric = numeric;
    this.dynamic = dynamic;
    this.value = value;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Nullable
  public String getDescription() {
    return description;
  }

  public boolean isNumeric() {
    return numeric;
  }

  public boolean isDynamic() {
    return dynamic;
  }

  @Nullable
  public String getValue() {
    return value;
  }

  @Nullable
  public Integer getMinValue() {
    return minValue;
  }

  @Nullable
  public Integer getMaxValue() {
    return maxValue;
  }

  @Override
  public String toString() {
    return "ApiOptionItem{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", numeric=" + numeric +
        ", dynamic=" + dynamic +
        ", value='" + value + '\'' +
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

    ApiOptionItem that = (ApiOptionItem) o;

    if (id != that.id) {
      return false;
    }
    if (numeric != that.numeric) {
      return false;
    }
    if (dynamic != that.dynamic) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    if (description != null ? !description.equals(that.description) : that.description != null) {
      return false;
    }
    if (value != null ? !value.equals(that.value) : that.value != null) {
      return false;
    }
    if (minValue != null ? !minValue.equals(that.minValue) : that.minValue != null) {
      return false;
    }
    return maxValue != null ? maxValue.equals(that.maxValue) : that.maxValue == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (numeric ? 1 : 0);
    result = 31 * result + (dynamic ? 1 : 0);
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (minValue != null ? minValue.hashCode() : 0);
    result = 31 * result + (maxValue != null ? maxValue.hashCode() : 0);
    return result;
  }
}
