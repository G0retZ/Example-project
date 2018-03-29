package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.code.CodeNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;

public class PasswordActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passwrod);
    setTitle(R.string.code);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case CodeNavigate.ENTER_APP:
        initExecutorStates();
        initGeoLocations();
        break;
      case ExecutorStateNavigate.AUTHORIZE:
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
