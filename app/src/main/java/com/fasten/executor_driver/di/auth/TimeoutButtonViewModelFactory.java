package com.fasten.executor_driver.di.auth;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.timeoutbutton.TimeoutButtonViewModel;
import com.fasten.executor_driver.presentation.timeoutbutton.TimeoutButtonViewModelImpl;

import javax.inject.Inject;

public class TimeoutButtonViewModelFactory implements ViewModelProvider.Factory {
	private final TimeoutButtonViewModel viewModel;

	@Inject
	TimeoutButtonViewModelFactory(TimeoutButtonViewModel viewModel) {
		this.viewModel = viewModel;
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		if (modelClass.isAssignableFrom(TimeoutButtonViewModelImpl.class)) {
			return (T) viewModel;
		}
		throw new IllegalArgumentException("Unknown class name");
	}
}
