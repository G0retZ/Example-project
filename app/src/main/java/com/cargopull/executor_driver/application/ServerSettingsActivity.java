package com.cargopull.executor_driver.application;

import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ServerSettingsActivity extends BaseActivity {

  private AppSettingsService appSettingsService;

  @Override
  public void setAppSettingsService(@NonNull AppSettingsService appSettingsService) {
    this.appSettingsService = appSettingsService;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_server_settings);
    TextView textView = findViewById(R.id.versionText);
    textView.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
    TextInputLayout ipAddressInputLayout = findViewById(R.id.ipAddressInputLayout);
    TextInputLayout portInputLayout = findViewById(R.id.portInputLayout);
    TextInputEditText ipAddressInput = findViewById(R.id.ipAddressInput);
    TextInputEditText portInput = findViewById(R.id.portInput);
    String oldPort = appSettingsService.getData("port");
    ipAddressInput.setText(appSettingsService.getData("address"));
    if (oldPort != null) {
      portInput.setText(oldPort);
    }
    findViewById(R.id.reset).setOnClickListener(v -> {
      ipAddressInputLayout.setError(null);
      portInput.setError(null);
      appSettingsService.saveData("address", null);
      appSettingsService.saveData("port", null);
      blockWithPending(true, "exit");
      navigate(CommonNavigate.EXIT);
    });
    findViewById(R.id.apply).setOnClickListener(v -> {
      ipAddressInputLayout.setError(null);
      portInput.setError(null);
      String address = extractStringFromView(ipAddressInput);
      if (!validateAddress(address)) {
        ipAddressInputLayout.setError("Неверный адрес");
        return;
      }
      String port = extractStringFromView(portInput);
      if (!validatePort(port)) {
        portInputLayout.setError("Неверный порт");
        return;
      }
      blockWithPending(true, "exit");
      appSettingsService.saveData("address", address);
      appSettingsService.saveData("port", port);
      navigate(CommonNavigate.EXIT);
    });
  }

  @Nullable
  private String extractStringFromView(EditText editText) {
    Editable text = editText.getText();
    return text == null ? null : text.toString();
  }

  private boolean validateAddress(@Nullable String address) {
    if (address == null) {
      return false;
    }
    String[] numbers = address.split("\\.");
    if (numbers.length != 4) {
      return false;
    }
    for (String number : numbers) {
      try {
        int num = Integer.valueOf(number);
        if (num < 0 || num > 255) {
          return false;
        }
      } catch (Exception e) {
        return false;
      }
    }
    return true;
  }

  private boolean validatePort(@Nullable String port) {
    if (port == null) {
      return false;
    }
    try {
      int num = Integer.valueOf(port);
      if (num < 0 || num > 65535) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
