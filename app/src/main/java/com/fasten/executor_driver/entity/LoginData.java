package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность входа в систему. Immutable.
 * Создается через конструктор с не нулевыми полями.
 * Любой сеттер возвращает новую сущность с заданными полями.
 */
public class LoginData {

  @NonNull
  private final String login;
  @NonNull
  private final String password;

  public LoginData(@NonNull String login, @NonNull String password) {
    this.login = login;
    this.password = password;
  }

  @NonNull
  public String getLogin() {
    return login;
  }

  public LoginData setLogin(@NonNull String login) {
    return new LoginData(login, password);
  }

  @NonNull
  public String getPassword() {
    return password;
  }

  public LoginData setPassword(@NonNull String password) {
    return new LoginData(login, password);
  }

  @Override
  public String toString() {
    return "LoginData{" +
        "login='" + login + '\'' +
        ", password='" + password + '\'' +
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

    LoginData loginData = (LoginData) o;

    if (!login.equals(loginData.login)) {
      return false;
    }
    return password.equals(loginData.password);
  }

  @Override
  public int hashCode() {
    int result = login.hashCode();
    result = 31 * result + password.hashCode();
    return result;
  }
}
