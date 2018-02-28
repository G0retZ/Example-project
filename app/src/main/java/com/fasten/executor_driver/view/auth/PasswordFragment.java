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
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.code.CodeViewActions;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewActions;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.fasten.executor_driver.view.BaseFragment;
import com.fasten.executor_driver.view.PermissionChecker;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает поле для ввода логина.
 */

public class PasswordFragment extends BaseFragment implements CodeViewActions,
    SmsButtonViewActions {

  private static final String[] PERMISSIONS = new String[]{Manifest.permission.RECEIVE_SMS};

  @Nullable
  private PermissionChecker permissionChecker;
  @Nullable
  private Disposable permissionDisposable;

  private CodeViewModel codeViewModel;
  private SmsButtonViewModel smsButtonViewModel;
  private ImageView codeInputUnderline;
  private EditText codeInput;
  private Button sendSmsRequest;
  private FrameLayout pendingIndicator;
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
  private boolean smsPending;
  private boolean codePending;

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
    codeInputUnderline = view.findViewById(R.id.codeInputUnderline);
    codeInput = view.findViewById(R.id.codeInput);
    sendSmsRequest = view.findViewById(R.id.sendSms);
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
    if (permissionDisposable != null) {
      permissionDisposable.dispose();
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void showCodeCheckPending(boolean pending) {
    codePending = pending;
    pendingIndicator.setVisibility(smsPending || codePending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void letIn() {
    navigate("enter");
  }

  @Override
  public void showCodeCheckError(@Nullable Throwable error) {
    if (error == null) {
//      codeInputUnderline.setError(null);
      codeInputUnderline.setImageResource(R.drawable.ic_code_input_activated);
    } else {
      codeInputUnderline.setImageResource(R.drawable.ic_code_input_error);
//      if (error instanceof NoNetworkException) {
//        codeInputUnderline.setError(getString(R.string.no_network_connection));
//      } else {
//        codeInputUnderline.setError(getString(R.string.invalid_code));
//      }
    }
  }

  // Замудренная логика форматировния ввода кода из СМС в режиме реального времени
  private void setTextListener() {
    codeInput.addTextChangedListener(new TextWatcher() {
      // Флаг, предотвращающий переолнение стека. Разделяет ручной ввод и форматирование.
      private boolean mFormatting;
      private int mAfter;

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      //called before the text is changed...
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mAfter = after; // флаг определения backspace.
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Игнорируем изменения, произведенные ниже (фильтруем действия нашего алгоритма).
        if (!mFormatting) {
          mFormatting = true;
          // Берем текущую позицию курсора после изменения текста.
          int selection = codeInput.getSelectionStart();
          // Берем текущую строку после изменения текста.
          String numbers = s.toString();
          // Если был удален не-цифровой символ, то удаляем цифровой символ слева,
          // и сдвигаем курсор влево.
          if (mAfter == 0) {
            if (selection == 15 || selection == 12) {
              numbers = new StringBuilder(numbers).deleteCharAt(--selection).toString();
            }
            if (selection == 8) {
              numbers = new StringBuilder(numbers).deleteCharAt(--selection).toString();
            }
            if (selection == 7) {
              numbers = new StringBuilder(numbers).deleteCharAt(--selection).toString();
            }
          }
          // Удаляем все нецифровые символы.
          numbers = numbers.replaceAll("[^\\d]", "");
          // Форматируем ввод в виде X   X   X   X.
          numbers = formatNumbersToCode(numbers);
          // Если курсор оказался перед открывающей скобкой, то помещаем его после нее.
          if (selection % 4 != 0 && selection < 12) {
            selection = selection - selection % 4;
          }
          // Защищаемся от {@link IndexOutOfBoundsException}
          selection = Math.min(selection, numbers.length());
          // Закидываем отформатированную строку в поле ввода
          codeInput.setText(numbers);
          // Сдвигаем курсор на нужную позицию
          codeInput.setSelection(selection);

          mFormatting = false;
          codeViewModel.setCode(numbers);
        }
      }
    });
  }

  private String formatNumbersToCode(String numbers) {
    numbers = numbers.replaceAll("(\\d)", "$1   ");
    if (numbers.length() > 13) {
      numbers = numbers.substring(0, 13);
    }
    return numbers;
  }

  @Override
  public void setSmsButtonText(@StringRes int res, @Nullable Long secondsLeft) {
    if (secondsLeft == null) {
      sendSmsRequest.setText(res);
    } else {
      sendSmsRequest.setText(getString(res, secondsLeft));
    }
  }

  @Override
  public void enableSmsButton(boolean enable) {
    sendSmsRequest.setOnClickListener(enable ? sendSmsClickListener : null);
  }

  @Override
  public void showSmsSendNetworkErrorMessage(boolean show) {
//    if (error != null) {
//      codeInputUnderline.setImageResource(R.drawable.ic_code_input_activated);
//      if (error instanceof NoNetworkException) {
//        codeInputUnderline.setError(getString(R.string.no_network_connection));
//      } else {
//        codeInputUnderline.setError(getString(R.string.server_fail));
//      }
//    }
  }

  @Override
  public void showSmsSendPending(boolean pending) {
    smsPending = pending;
    pendingIndicator.setVisibility(smsPending || codePending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (permissionChecker != null) {
      permissionChecker.onResult(requestCode, permissions, grantResults);
    }
  }

  private void autoSendSmsRequest() {
    if (!smsSent) {
      sendSmsRequest.post(sendSmsRequest::performClick);
    }
  }

  private void checkPermissions() {
    permissionChecker = new PermissionChecker(1337);
    permissionDisposable = permissionChecker.check(this, context, PERMISSIONS)
        .doFinally(() -> permissionChecker = null)
        .subscribe(this::autoSendSmsRequest, t -> autoSendSmsRequest());
  }
}
