package com.fasten.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные об услуге.
 */
@SuppressWarnings("unused")
public class ApiServiceItem {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("name")
  private String name;
  @Nullable
  @SerializedName("price")
  private Long price;
  @SerializedName("value")
  private boolean value;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiServiceItem() {
  }

  public ApiServiceItem(long id, @Nullable String name, @Nullable Long price, boolean value) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Nullable
  public Long getPrice() {
    return price;
  }

  public boolean getValue() {
    return value;
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

    ApiServiceItem that = (ApiServiceItem) o;

    if (id != that.id) {
      return false;
    }
    if (value != that.value) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    return price != null ? price.equals(that.price) : that.price == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (price != null ? price.hashCode() : 0);
    result = 31 * result + (value ? 1 : 0);
    return result;
  }
}
