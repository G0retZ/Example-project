package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;

public class PasswordActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passwrod);
    setTitle(R.string.enter);
  }

  @Override
  public void navigate(@NonNull String destination) {
    if (destination.equals("enter")) {
      Intent intent = new Intent(this, MapActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finish();
    }
  }
}
