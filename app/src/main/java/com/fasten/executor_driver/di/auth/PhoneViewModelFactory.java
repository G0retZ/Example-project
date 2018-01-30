package com.fasten.executor_driver.di.auth;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;

import javax.inject.Inject;

public class PhoneViewModelFactory implements ViewModelProvider.Factory {
	private final PhoneViewModel viewModel;

	@Inject
	public PhoneViewModelFactory(PhoneViewModel viewModel) {
		this.viewModel = viewModel;
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		if (modelClass.isAssignableFrom(PhoneViewModelImpl.class)) {
			return (T) viewModel;
		}
		throw new IllegalArgumentException("Unknown class name");
	}
}
