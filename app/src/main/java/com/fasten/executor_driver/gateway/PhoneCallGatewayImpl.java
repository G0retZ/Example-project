package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.interactor.auth.PhoneCallGateway;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class PhoneCallGatewayImpl implements PhoneCallGateway {

	@NonNull
	private final ApiService api;

	@Inject
	public PhoneCallGatewayImpl(@NonNull ApiService api) {
		this.api = api;
	}

	@NonNull
	@Override
	public Completable callMe(@NonNull String phoneNumber) {
		return api.callMeCode(phoneNumber)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.single());
	}
}
