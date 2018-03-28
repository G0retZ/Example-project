package com.fasten.executor_driver.application;

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
    if (destination.equals(ServicesOptionsNavigate.READY_FOR_ORDERS)) {
      System.out.println(destination);
    } else {
      super.navigate(destination);
    }
  }
}
