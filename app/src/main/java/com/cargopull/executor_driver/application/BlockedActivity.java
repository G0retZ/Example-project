package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;

public class BlockedActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_blocked);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, MenuActivity.class))
      );
    }
  }

  @Override
  public void showExecutorStatusMessage(@NonNull String message) {
    ((TextView) findViewById(R.id.blockText)).setText(message);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
