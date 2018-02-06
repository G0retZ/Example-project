package com.fasten.executor_driver.view.auth;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentFilter;
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
import com.fasten.executor_driver.view.PermissionChecker;
import com.jakewharton.rxbinding2.widget.RxTextView;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает поле для ввода логина.
 */

public class PasswordFragment extends BaseFragment implements CodeViewActions,
    SmsButtonViewActions {

  private static final String[] PERMISSIONS = new String[]{Manifest.permission.RECEIVE_SMS};

  private PermissionChecker permissionChecker;

  private CodeViewModel codeViewModel;
  private SmsButtonViewModel smsButtonViewModel;
  private TextInputLayout codeInputLayout;
  private TextInputEditText codeInput;
  private Button sendSmsRequest;
  private ProgressBar pendingIndicator;
  private ProgressBar sendingIndicator;
  private final OnClickListener sendSmsClickListener = v -> {
    smsSent = true;
    smsButtonViewModel.sendMeSms();
  };
  private Context context;

  private ViewModelProvider.Factory codeViewModelFactory;
  private ViewModelProvider.Factory buttonViewModelFactory;
  private SmsReceiver smsReceiver;
  private Disposable smsCodeDisposable;
  private boolean smsSent;

  @Inject
  public void setCodeViewModelFactory(@Named("code") Factory codeViewModelFactory) {
    this.codeViewModelFactory = codeViewModelFactory;
  }

  @Inject
  public void setButtonViewModelFactory(@Named("button") Factory buttonViewModelFactory) {
    this.buttonViewModelFactory = buttonViewModelFactory;
  }

  @Inject
  public void setSmsReceiver(SmsReceiver smsReceiver) {
    this.smsReceiver = smsReceiver;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
    codeViewModel = ViewModelProviders.of(this, codeViewModelFactory).get(CodeViewModelImpl.class);
    smsButtonViewModel = ViewModelProviders.of(this, buttonViewModelFactory)
        .get(SmsButtonViewModelImpl.class);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    smsSent = savedInstanceState != null;
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(SmsReceiver.ACTION);
    intentFilter.setPriority(999);
    context.registerReceiver(smsReceiver, intentFilter);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auth_password, container, false);
    codeInputLayout = view.findViewById(R.id.codeInputLayout);
    codeInput = view.findViewById(R.id.codeInput);
    sendSmsRequest = view.findViewById(R.id.sendSms);
    pendingIndicator = view.findViewById(R.id.pending);
    sendingIndicator = view.findViewById(R.id.sending);

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
    checkPermissions();
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    smsCodeDisposable = smsReceiver.getCodeFromSms().subscribe(text -> {
      codeInput.setText(text);
      codeInput.setSelection(text.length());
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    smsCodeDisposable.dispose();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    context.unregisterReceiver(smsReceiver);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
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
      sendSmsRequest.setText(R.string.get_code_from_sms);
    } else {
      sendSmsRequest.setText(getString(R.string.repeat_code_from_sms, secondsLeft));
    }
  }

  @Override
  public void setSmsButtonResponsive(boolean responsive) {
    sendSmsRequest.setOnClickListener(responsive ? sendSmsClickListener : null);
  }

  @Override
  public void showSmsSendError(@Nullable Throwable error) {
    if (error != null) {
      if (error instanceof NoNetworkException) {
        codeInputLayout.setError(getString(R.string.no_network_connection));
      } else {
        codeInputLayout.setError(getString(R.string.server_fail));
      }
    }
  }

  @Override
  public void showSmsSendPending(boolean pending) {
    sendSmsRequest.setVisibility(pending ? View.INVISIBLE : View.VISIBLE);
    sendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    permissionChecker.onResult(requestCode, permissions, grantResults);
  }

  private void autoSendSmsRequest() {
    if (!smsSent) {
      sendSmsRequest.post(sendSmsRequest::performClick);
    }
  }

  private void checkPermissions() {
    permissionChecker = new PermissionChecker(1337);
    permissionChecker.check(this, context, PERMISSIONS)
        .doFinally(() -> permissionChecker = null)
        .subscribe(this::autoSendSmsRequest, t -> autoSendSmsRequest());
  }
}
