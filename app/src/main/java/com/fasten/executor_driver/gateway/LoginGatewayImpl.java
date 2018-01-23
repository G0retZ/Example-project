package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.model.ApiLogin;
import com.fasten.executor_driver.interactor.auth.LoginGateway;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class LoginGatewayImpl implements LoginGateway {

	private final ApiService api;

	public LoginGatewayImpl(ApiService api) {
		this.api = api;
	}

	@NonNull
	@Override
	public Completable checkLogin(@NonNull String login) {
		return api.checkLogin(new ApiLogin(login, ""))
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.single());
	}

}
