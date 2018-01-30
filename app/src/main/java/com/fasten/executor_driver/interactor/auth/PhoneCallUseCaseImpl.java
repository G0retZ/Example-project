package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.Validator;

import javax.inject.Inject;

import io.reactivex.Completable;

public class PhoneCallUseCaseImpl implements PhoneCallUseCase {

	@NonNull
	private final PhoneCallGateway gateway;
	@NonNull
	private final Validator<String> phoneNumberValidator;

	@Inject
	public PhoneCallUseCaseImpl(@NonNull PhoneCallGateway gateway, @NonNull Validator<String> phoneNumberValidator) {
		this.gateway = gateway;
		this.phoneNumberValidator = phoneNumberValidator;
	}

	@NonNull
	@Override
	public Completable callMe(@Nullable String phoneNumber) {
		return Completable.create(e -> {
			if (phoneNumber != null && phoneNumberValidator.validate(phoneNumber)) {
				gateway.callMe(phoneNumber).subscribe(e::onComplete, e::onError);
			} else {
				e.onError(new ValidationException());
			}
		});
	}
}
