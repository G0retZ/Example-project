package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.services.ServicesOptionsNavigate;

public class ServicesActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_services);
    setTitle(R.string.select_services);
  }

  @Override
  public void navigate(@NonNull String destination) {
    Intent intent;
    switch (destination) {
      case ServicesOptionsNavigate.READY_FOR_ORDERS:
        intent = new Intent(this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        break;
    }
  }
}
