package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.code.CodeNavigate;

public class PasswordActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passwrod);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case CodeNavigate.ENTER_APP:
        ((MainApplication) getApplication()).initApplication();
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
