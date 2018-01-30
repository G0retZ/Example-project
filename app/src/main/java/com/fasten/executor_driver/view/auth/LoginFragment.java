package com.fasten.executor_driver.view.auth;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.phone.PhoneViewActions;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.view.BaseFragment;
import com.jakewharton.rxbinding2.widget.RxTextView;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.disposables.Disposable;

/**
 * Отображает поле для ввода логина.
 */

public class LoginFragment extends BaseFragment implements PhoneViewActions {

	private PhoneViewModel phoneViewModel;
	private TextInputLayout phoneInputLayout;
	private TextInputEditText phoneInput;
	private Button goNext;
	private ProgressBar pendingIndicator;
	private Disposable textWatcherDisposable;


	private ViewModelProvider.Factory viewModelFactory;

	@Inject
	public void setViewModelFactory(@Named("phone") ViewModelProvider.Factory viewModelFactory) {
		this.viewModelFactory = viewModelFactory;
	}

	@Override
	protected void onDependencyInject(AppComponent appComponent) {
		// Required by Dagger2 for field injection
		appComponent.inject(this);
		phoneViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhoneViewModelImpl.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
		phoneInputLayout = view.findViewById(R.id.phoneInputLayout);
		phoneInput = view.findViewById(R.id.phoneInput);
		goNext = view.findViewById(R.id.goNext);
		pendingIndicator = view.findViewById(R.id.pending);

		goNext.setOnClickListener(v -> phoneViewModel.nextClicked());
		phoneViewModel.getViewStateLiveData().observe(this, viewState -> {
			if (viewState != null) {
				viewState.apply(this);
			}
		});
		setTextListener();
		return view;
	}


	@Override
	public void proceedNext(@NonNull String login) {
		System.out.println(login);
	}

	@Override
	public void enableButton(boolean enable) {
		goNext.setEnabled(enable);
	}

	@Override
	public void showError(@Nullable Throwable error) {
		if (error == null) {
			phoneInputLayout.setError(null);
		} else {
			if (error instanceof NoNetworkException) {
				phoneInputLayout.setError(getString(R.string.no_network_connection));
			} else {
				phoneInputLayout.setError(getString(R.string.phone_not_found));
			}
		}
	}

	private void setTextListener() {
		textWatcherDisposable = RxTextView.textChanges(phoneInput)
				.subscribe(phone -> {
//					if (phone.length() <= 4) phone = "+7 (";
//					phoneInput.setText(phone);
					phoneViewModel.phoneNumberChanged(phone.toString());
				});
	}

	@Override
	public void onDestroyView() {
		textWatcherDisposable.dispose();
		super.onDestroyView();
	}

	@Override
	public void showPending(boolean pending) {
		pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
	}
}
