package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;

public class LoginActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);
    setTitle(R.string.authorization);
  }

  @Override
  public void navigate(@NonNull String destination) {
    if (destination.equals("next")) {
      startActivity(new Intent(this, PasswordActivity.class));
    } else {
      super.navigate(destination);
    }
  }
}
