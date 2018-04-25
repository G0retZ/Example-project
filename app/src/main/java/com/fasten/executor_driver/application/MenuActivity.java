package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.menu.MenuNavigate;

public class MenuActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case MenuNavigate.PROFILE:
        onBackPressed();
        break;
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        finish();
        break;
      case MenuNavigate.MESSAGES:
        onBackPressed();
        break;
      case MenuNavigate.HISTORY:
        onBackPressed();
        break;
      case MenuNavigate.OPERATOR:
        onBackPressed();
        break;
      case MenuNavigate.VEHICLES:
        onBackPressed();
        break;
      default:
        super.navigate(destination);
    }
  }
}
