package com.fasten.executor_driver.application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.view.GeolocationResolutionFragment;

public class GeolocationResolutionActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_geo_resolution);
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
      case GeolocationResolutionFragment.NAVIGATE_TO_RESOLVED:
        ((MainApplication) getApplication()).loadApplication();
        super.onBackPressed();
        break;
    }
    // никуда не переходим
  }

  @Override
  public void onBackPressed() {
    // никуда не переходим
  }
}
