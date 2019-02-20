package com.cargopull.executor_driver.view.auth;

import android.animation.ObjectAnimator;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.code.CodeViewActions;
import com.cargopull.executor_driver.presentation.code.CodeViewModel;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModel;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.cargopull.executor_driver.view.BaseFragment;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;

/**
 * Отображает поле для ввода логина.
 */

public class PasswordFragment extends BaseFragment implements CodeViewActions {

  private CodeViewModel codeViewModel;
  private CodeHeaderViewModel codeHeaderViewModel;
  private SmsButtonViewModel smsButtonViewModel;
  private ShakeItPlayer shakeItPlayer;
  private EditText codeInput;
  private Disposable textDisposable;

  @Inject
  public void setCodeViewModel(@NonNull CodeViewModel codeViewModel) {
    this.codeViewModel = codeViewModel;
  }

  @Inject
  public void setCodeHeaderViewModel(@NonNull CodeHeaderViewModel codeHeaderViewModel) {
    this.codeHeaderViewModel = codeHeaderViewModel;
  }

  @Inject
  public void setSmsButtonViewModel(@NonNull SmsButtonViewModel smsButtonViewModel) {
    this.smsButtonViewModel = smsButtonViewModel;
  }

  @Inject
  public void setShakeItPlayer(@NonNull ShakeItPlayer shakeItPlayer) {
    this.shakeItPlayer = shakeItPlayer;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auth_password, container, false);
    codeInput = view.findViewById(R.id.codeInput);
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      codeInput.setLetterSpacing(1.2f);
    }
    view.findViewById(R.id.sendSms).setOnClickListener(v -> sendSmsRequest());
    return view;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    codeViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    codeViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    codeHeaderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    smsButtonViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    //noinspection Convert2MethodRef
    textDisposable = RxTextView.afterTextChangeEvents(codeInput)
        .map(TextViewAfterTextChangeEvent::editable)
        .filter(val -> val != null)
        .map(CharSequence::toString)
        .subscribe(codeViewModel::setCode);
  }

  @Override
  public void animateError() {
    shakeItPlayer.shakeIt(R.raw.single_shot_vibro);
    float density = getResources().getDisplayMetrics().density;
    ObjectAnimator animator = ObjectAnimator.ofFloat(codeInput, "translationX", 0, 10 * density, -10 * density, 0);
    animator.setDuration(200);
    animator.setInterpolator(new ShakeInterpolator());
    animator.start();
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState == null) {
      sendSmsRequest();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    textDisposable.dispose();
  }

  private void sendSmsRequest() {
    codeInput.setText("");
    smsButtonViewModel.sendMeSms();
  }
}
