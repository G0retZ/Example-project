package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.menu.MenuNavigate;

public class MenuActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(R.string.menu);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return (true);
    }
    return (super.onOptionsItemSelected(item));
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case MenuNavigate.PROFILE:
        finish();
        break;
      case MenuNavigate.BALANCE:
        finish();
        break;
      case MenuNavigate.MESSAGES:
        finish();
        break;
      case MenuNavigate.HISTORY:
        finish();
        break;
      case MenuNavigate.OPERATOR:
        finish();
        break;
      case MenuNavigate.VEHICLES:
        finish();
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        break;
      default:
        super.navigate(destination);
    }
  }
}
