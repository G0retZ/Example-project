package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fasten.executor_driver.R;

public class AuthActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);
    setTitle(R.string.authorization);
  }

}
