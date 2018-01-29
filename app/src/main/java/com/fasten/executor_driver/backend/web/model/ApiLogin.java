package com.fasten.executor_driver.backend.web.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект Логина для сериализации в JSON для отправки в АПИ
 */

public class ApiLogin {

	@NonNull
	@SerializedName("username")
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
	String getName() {
		return name;
	}

	@NonNull
	String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "ApiLogin{" +
				"name='" + name + '\'' +
				", password='" + password + '\'' +
				'}';
	}

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ApiLogin apiLogin = (ApiLogin) o;

		if (!name.equals(apiLogin.name)) return false;
		return password.equals(apiLogin.password);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + password.hashCode();
		return result;
	}
}
