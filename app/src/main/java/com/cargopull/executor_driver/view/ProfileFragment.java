package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.di.AppComponent;
import javax.inject.Inject;

/**
 * Отображает поле для ввода логина.
 */

public class ProfileFragment extends BaseFragment {

  private TextView phoneNumberText;

  private AppSettingsService appSettings;

  @Inject
  public void setAppSettings(@NonNull AppSettingsService appSettings) {
    this.appSettings = appSettings;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    phoneNumberText = (TextView) view;
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
    // Если не было сохраненного состояния (первый запуск)
    if (savedInstanceState == null) {
      String lastPhoneNumber = appSettings.getData("authorizationLogin");
      if (lastPhoneNumber == null) {
        phoneNumberText.setText("+7 (");
      } else {
        lastPhoneNumber = formatNumbersToPhone(lastPhoneNumber);
        phoneNumberText.setText(lastPhoneNumber);
      }
    }
  }

  private String formatNumbersToPhone(String numbers) {
    return numbers.replaceFirst("(\\d)", "+$1 (")
        .replaceFirst("(\\(\\d{3})", "$1) ")
        .replaceFirst("( \\d{3})", "$1-")
        .replaceFirst("(-\\d{2})", "$1-");
  }
}
