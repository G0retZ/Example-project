package com.fasten.executor_driver.application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.view.GeolocationResolutionFragment;

public class GeolocationResolutionActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_geo_resolution);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("");
      actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case GeolocationResolutionFragment.NAVIGATE_TO_SETTINGS:
        startActivity(
            new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(
                    Uri.fromParts("package", getPackageName(), null)
                )
        );
        break;
      case ExecutorStateNavigate.AUTHORIZE:
        super.navigate(destination);
        break;
      case GeolocationResolutionFragment.NAVIGATE_TO_RESOLVED:
        finish();
        break;
    }
  }
}
