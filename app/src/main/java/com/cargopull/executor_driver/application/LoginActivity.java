package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.phone.PhoneNavigate;

public class LoginActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case PhoneNavigate.PASSWORD:
        startActivity(new Intent(this, PasswordActivity.class));
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
