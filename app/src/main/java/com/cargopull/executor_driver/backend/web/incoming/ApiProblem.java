package com.cargopull.executor_driver.backend.web.incoming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Problem;
import com.google.gson.annotations.SerializedName;

public class ApiProblem {

  @SerializedName("id")
  private int id;
  @Nullable
  @SerializedName("description")
  private String description;
  @Nullable
  @SerializedName("name")
  private String name;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiProblem() {
  }

  public ApiProblem(int id, @Nullable String description, @Nullable String name) {
    this.id = id;
    this.description = description;
    this.name = name;
  }

  public ApiProblem(@NonNull Problem problem) {
    this.id = problem.getId();
    this.description = problem.getName();
    this.name = problem.getUnusedName();
  }

  public int getId() {
    return id;
  }

  @Nullable
  public String getDescription() {
    return description;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ApiProblem that = (ApiProblem) o;

    if (id != that.id) {
      return false;
    }
    if (description != null ? !description.equals(that.description) : that.description != null) {
      return false;
    }
    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
