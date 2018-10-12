package com.cargopull.executor_driver.backend.web.outgoing;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект опции ТС для сериализации в JSON для отправки в АПИ.
 */
public class ApiOptionItem {

  @SerializedName("id")
  @Expose
  private final long id;
  @NonNull
  @SerializedName("value")
  @Expose
  private final String value;

  public ApiOptionItem(long id, @NonNull String value) {
    this.id = id;
    this.value = value;
  }

  long getId() {
    return id;
  }

  @NonNull
  String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "ApiOptionItem{" +
        "id=" + id +
        ", value='" + value + '\'' +
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

    ApiOptionItem that = (ApiOptionItem) o;

    return id == that.id && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + value.hashCode();
    return result;
  }
}
