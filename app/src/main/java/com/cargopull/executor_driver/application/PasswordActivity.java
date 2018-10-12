package com.cargopull.executor_driver.application;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.code.CodeNavigate;

public class PasswordActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passwrod);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case CodeNavigate.ENTER_APP:
        ((MainApplication) getApplication()).initServerConnection();
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
