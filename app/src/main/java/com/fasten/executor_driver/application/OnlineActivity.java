package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import com.fasten.executor_driver.R;

public class OnlineActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_online);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("");
      actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }
}
