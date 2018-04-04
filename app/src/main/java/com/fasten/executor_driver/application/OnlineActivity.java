package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;

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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.online_map, menu);
    MenuItem menuItem = menu.findItem(R.id.goOnline);
    if (menuItem != null) {
      View view = menuItem.getActionView();
      if (view != null) {
        ((CompoundButton) view.findViewById(R.id.goOnlineSwitch)).setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
              if (isChecked) {
                startActivity(new Intent(this, ServicesActivity.class));
              }
            });
      }
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        startActivity(new Intent(this, MenuActivity.class));
        return (true);
    }
    return (super.onOptionsItemSelected(item));
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
