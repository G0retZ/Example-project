package com.fasten.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий имя какого-либо параметра
 */
class ApiParam {

  @Nullable
  @SerializedName("name")
  private String name;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  ApiParam() {
  }

  ApiParam(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ApiParam{" +
        "name='" + name + '\'' +
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

    ApiParam that = (ApiParam) o;

    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
