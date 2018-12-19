package com.cargopull.executor_driver.view.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.code.CodeViewActions;
import com.cargopull.executor_driver.presentation.code.CodeViewModel;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewActions;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModel;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewActions;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.cargopull.executor_driver.view.BaseFragment;
import javax.inject.Inject;

/**
 * Отображает поле для ввода логина.
 */

public class PasswordFragment extends BaseFragment implements CodeViewActions,
    CodeHeaderViewActions, SmsButtonViewActions {

  private CodeViewModel codeViewModel;
  private CodeHeaderViewModel codeHeaderViewModel;
  private SmsButtonViewModel smsButtonViewModel;
  private TextView networkErrorText;
  private ConstraintLayout codeInputLayout;
  private TextView codeErrorText;
  private TextView codeInputCaption;
  private ImageView codeInputUnderline;
  private EditText codeInput;
  private Button sendSmsRequest;

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

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auth_password, container, false);
    networkErrorText = view.findViewById(R.id.networkErrorText);
    codeInputLayout = view.findViewById(R.id.codeInputLayout);
    codeErrorText = codeInputLayout.findViewById(R.id.codeErrorText);
    codeInputCaption = codeInputLayout.findViewById(R.id.codeInputCaption);
    codeInputUnderline = codeInputLayout.findViewById(R.id.codeInputUnderline);
    codeInput = codeInputLayout.findViewById(R.id.codeInput);
    sendSmsRequest = view.findViewById(R.id.sendSms);
    sendSmsRequest.setOnClickListener(v -> sendSmsRequest());
    setTextListener();
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
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState == null) {
      sendSmsRequest();
    }
  }

  @Override
  public void enableInputField(boolean enable) {
    codeInput.setEnabled(enable);
  }

  @Override
  public void setUnderlineImage(@DrawableRes int resId) {
    codeInputUnderline.setImageResource(resId);
  }

  @Override
  public void showCodeCheckPending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showCodeCheckError(boolean show) {
    codeErrorText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showCodeCheckNetworkErrorMessage(boolean show) {
    if (show) {
      networkErrorText.setText(R.string.code_network_error);
    }
    networkErrorText.setVisibility(show ? View.VISIBLE : View.GONE);
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
    sendSmsRequest.setEnabled(enable);
  }

  @Override
  public void showSmsSendNetworkErrorMessage(boolean show) {
    if (show) {
      networkErrorText.setText(R.string.sms_network_error);
    }
    codeInputLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    networkErrorText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showSmsSendPending(boolean pending) {
    showPending(pending, toString() + "1");
  }

  private void sendSmsRequest() {
    codeInput.setText("");
    smsButtonViewModel.sendMeSms();
  }

  @Override
  public void setDescriptiveHeaderText(int textId, @NonNull String phoneNumber) {
    codeInputCaption.setText(getString(textId, phoneNumber));
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
          // Берем текущую строку после изменения текста.
          String numbers = s.toString();
          // Удаляем все нецифровые символы.
          numbers = numbers.replaceAll("[^\\d]", "");
          // Если был удален не-цифровой символ, то удаляем цифровой символ слева,
          // и сдвигаем курсор влево.
          if (mAfter == 0 && numbers.length() > 0 && codeInput.getSelectionStart() % 4 != 0) {
            numbers = new StringBuilder(numbers).deleteCharAt(numbers.length() - 1).toString();
          }
          // Форматируем ввод в виде X   X   X   X.
          numbers = formatNumbersToCode(numbers);
          // Закидываем отформатированную строку в поле ввода
          codeInput.setText(numbers);
          // Устанавливаем курсор в конце
          codeInput.setSelection(numbers.length());

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
}
