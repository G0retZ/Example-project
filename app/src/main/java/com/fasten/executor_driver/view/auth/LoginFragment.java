package com.fasten.executor_driver.view.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.phone.PhoneViewActions;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.view.BaseFragment;
import javax.inject.Inject;

/**
 * Отображает поле для ввода логина.
 */

public class LoginFragment extends BaseFragment implements PhoneViewActions {

  private PhoneViewModel phoneViewModel;
  private EditText phoneInput;
  private Button goNext;

  private AppSettingsService appSettings;

  @Inject
  public void setAppSettings(@NonNull AppSettingsService appSettings) {
    this.appSettings = appSettings;
  }

  @Inject
  public void setPhoneViewModel(@NonNull PhoneViewModel phoneViewModel) {
    this.phoneViewModel = phoneViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
    phoneInput = view.findViewById(R.id.phoneInput);
    goNext = view.findViewById(R.id.goNext);
    view.findViewById(R.id.becomeDriver).setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse("https://vezetdobro.ru/drivers"));
      startActivity(intent);
    });

    goNext.setOnClickListener(v -> phoneViewModel.nextClicked());
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
    phoneViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    phoneViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    // Если не было сохраненного состояния (первый запуск)
    if (savedInstanceState == null) {
      String lastPhoneNumber = appSettings.getData("authorizationLogin");
      if (lastPhoneNumber == null) {
        phoneInput.setText("+7 (");
        phoneInput.setSelection(4);
      } else {
        lastPhoneNumber = formatNumbersToPhone(lastPhoneNumber);
        phoneInput.setText(lastPhoneNumber);
        phoneInput.setSelection(lastPhoneNumber.length());
        phoneViewModel.phoneNumberChanged(lastPhoneNumber);
      }
    }
  }

  @Override
  public void enableButton(boolean enable) {
    goNext.setEnabled(enable);
  }

  // Замудренная логика форматировния ввода номера телефона в режиме реального времени
  private void setTextListener() {
    phoneInput.addTextChangedListener(new TextWatcher() {
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
          int selection = phoneInput.getSelectionStart();
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
          if (numbers.isEmpty()) {
            // Если строка оказалась пуста, то добавляем код страны 7.
            numbers = "7";
          } else if (!numbers.substring(0, 1).equals("7")) {
            // Если был ввод цифы перед кодом страны, то меняем их местами.
            numbers = new StringBuilder(numbers).deleteCharAt(1).insert(0, "7").toString();
          }
          // Форматируем ввод в виде +7 (XXX) XXX-XX-XX.
          numbers = formatNumbersToPhone(numbers);
          // Если курсор оказался перед открывающей скобкой, то помещаем его после нее.
          if (selection < 5) {
            selection = 5;
          } else {
            if (mAfter != 0) {
              if (selection == 7 || selection == 8) {
                selection++;
              }
              if (selection == 8 || selection == 9) {
                selection++;
              }
              if (selection == 12 || selection == 13) {
                selection++;
              }
              if (selection == 15 || selection == 16) {
                selection++;
              }
            } else {
              if (selection == 16 || selection == 13) {
                selection--;
              }
              numbers = numbers.replaceAll("-$", "");
            }
          }
          // Защищаемся от {@link IndexOutOfBoundsException}
          selection = Math.min(selection, numbers.length());
          // Закидываем отформатированную строку в поле ввода
          phoneInput.setText(numbers);
          // Сдвигаем курсор на нужную позицию
          phoneInput.setSelection(selection);

          mFormatting = false;
          phoneViewModel.phoneNumberChanged(numbers);
        }
      }
    });
  }

  private String formatNumbersToPhone(String numbers) {
    return numbers.replaceFirst("(\\d)", "+$1 (")
        .replaceFirst("(\\(\\d{3})", "$1) ")
        .replaceFirst("( \\d{3})", "$1-")
        .replaceFirst("(-\\d{2})", "$1-");
  }
}
