package com.fasten.executor_driver.interactor.login;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;

import io.reactivex.Completable;

public class LoginUseCaseImpl implements LoginUseCase {

	private final LoginGateway gateway;
	private final Validator<String> loginValidator;
	private final Validator<String> passwordValidator;

	@NonNull
	private LoginData loginData = new LoginData("", "");

	LoginUseCaseImpl(LoginGateway gateway, Validator<String> loginValidator, Validator<String> passwordValidator) {
		this.gateway = gateway;
		this.loginValidator = loginValidator;
		this.passwordValidator = passwordValidator;
	}

	@NonNull
	@Override
	public Completable setLogin(String login) {
		return Completable.create(e -> {
			if (loginValidator.validate(login)) {
				loginData = loginData.setLogin(login);
				e.onComplete();
			} else {
				e.onError(new IllegalArgumentException());
			}
		});
	}

	@NonNull
	@Override
	public Completable setPassword(String password) {
		return Completable.create(e -> {
			if (passwordValidator.validate(password)) {
				loginData = loginData.setPassword(password);
				e.onComplete();
			} else {
				e.onError(new IllegalArgumentException());
			}
		});
	}

	@NonNull
	@Override
	public Completable login() {
		return Completable.create(e -> {
					if (loginValidator.validate(loginData.getLogin())
							&& passwordValidator.validate(loginData.getPassword())) {
						e.onComplete();
					} else {
						e.onError(new IllegalArgumentException());
					}
				}).andThen(gateway.login(loginData));
	}

}
