package com.fasten.executor_driver.di.auth;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;

import javax.inject.Inject;

public class CodeViewModelFactory implements ViewModelProvider.Factory {
	private final CodeViewModel viewModel;

	@Inject
	CodeViewModelFactory(CodeViewModel viewModel) {
		this.viewModel = viewModel;
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		if (modelClass.isAssignableFrom(CodeViewModelImpl.class)) {
			return (T) viewModel;
		}
		throw new IllegalArgumentException("Unknown class name");
	}
}
