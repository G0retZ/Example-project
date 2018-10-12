package com.cargopull.executor_driver.backend.web.outgoing;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект Логина для сериализации в JSON для отправки в АПИ.
 */
public class ApiLogin {

  @NonNull
  @SerializedName("login")
  @Expose
  private final String name;
  @NonNull
  @SerializedName("password")
  @Expose
  private final String password;

  public ApiLogin(@NonNull String name, @NonNull String password) {
    this.name = name;
    this.password = password;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "ApiLogin{" +
        "name='" + name + '\'' +
        ", password='" + password + '\'' +
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

    ApiLogin apiLogin = (ApiLogin) o;

    return name.equals(apiLogin.name) && password.equals(apiLogin.password);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + password.hashCode();
    return result;
  }
}
