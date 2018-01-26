package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.backend.web.model.ApiLogin;

import io.reactivex.Completable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Общение с сервером API
 */
public interface ApiService {

	/*
	 *  Проверка логина
	 */
	@POST("drivers/checkLogin")
	Completable checkLogin(@NonNull @Body ApiLogin apiLogin);

	/*
	 *  Авторизация
	 */
	@POST("drivers/auth")
	Completable authorize(@NonNull @Body ApiLogin apiLogin);

}
