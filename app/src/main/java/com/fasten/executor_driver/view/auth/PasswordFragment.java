package com.fasten.executor_driver.view.auth;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.code.CodeViewActions;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewActions;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.fasten.executor_driver.view.BaseFragment;
import com.jakewharton.rxbinding2.widget.RxTextView;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает поле для ввода логина.
 */

public class PasswordFragment extends BaseFragment implements CodeViewActions,
    SmsButtonViewActions {

  private CodeViewModel codeViewModel;
  private SmsButtonViewModel smsButtonViewModel;
  private TextInputLayout codeInputLayout;
  private TextInputEditText codeInput;
  private Button sendSms;
  private ProgressBar pendingIndicator;
  private final OnClickListener sendSmsClickListener = v -> {
    if (smsButtonViewModel.buttonClicked()) {
      codeViewModel.sendMeSms();
    }
  };

  private ViewModelProvider.Factory codeViewModelFactory;
  private ViewModelProvider.Factory buttonViewModelFactory;

  @Inject
  public void setCodeViewModelFactory(@Named("code") Factory codeViewModelFactory) {
    this.codeViewModelFactory = codeViewModelFactory;
  }

  @Inject
  public void setButtonViewModelFactory(@Named("button") Factory buttonViewModelFactory) {
    this.buttonViewModelFactory = buttonViewModelFactory;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
    codeViewModel = ViewModelProviders.of(this, codeViewModelFactory).get(CodeViewModelImpl.class);
    smsButtonViewModel = ViewModelProviders.of(this, buttonViewModelFactory)
        .get(SmsButtonViewModelImpl.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auth_password, container, false);
    codeInputLayout = view.findViewById(R.id.codeInputLayout);
    codeInput = view.findViewById(R.id.codeInput);
    sendSms = view.findViewById(R.id.sendSms);
    pendingIndicator = view.findViewById(R.id.pending);

    codeViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    smsButtonViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    setTextListener();
    if (savedInstanceState == null) {
      sendSms.post(sendSms::performClick);
    }
    return view;
  }

  @Override
  public void showCodeCheckPending(boolean pending) {
    pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void letIn() {
    navigate("enter");
  }

  @Override
  public void showCodeCheckError(@Nullable Throwable error) {
    if (error == null) {
      codeInputLayout.setError(null);
    } else {
      if (error instanceof NoNetworkException) {
        codeInputLayout.setError(getString(R.string.no_network_connection));
      } else {
        codeInputLayout.setError(getString(R.string.invalid_code));
      }
    }
  }

  // Замудренная логика форматировния ввода номера телефона в режиме реального времени
  private void setTextListener() {
    RxTextView.textChanges(codeInput).subscribe(code -> codeViewModel.setCode(code.toString()));
  }

  @Override
  public void showSmsButtonTimer(@Nullable Long secondsLeft) {
    if (secondsLeft == null) {
      sendSms.setText(R.string.get_code_from_sms);
    } else {
      sendSms.setText(getString(R.string.repeat_code_from_sms, secondsLeft));
    }
  }

  @Override
  public void setSmsButtonResponsive(boolean responsive) {
    sendSms.setOnClickListener(responsive ? sendSmsClickListener : null);
  }
}
