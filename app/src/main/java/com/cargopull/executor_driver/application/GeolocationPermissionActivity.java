package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.view.GeolocationPermissionFragment;

public class GeolocationPermissionActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_geo_permission);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case GeolocationPermissionFragment.NAVIGATE_TO_PERMISSION_SETTINGS:
        startActivity(
            new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(
                    Uri.fromParts("package", getPackageName(), null)
                )
        );
        break;
      case GeolocationPermissionFragment.NAVIGATE_TO_PERMISSION_GRANTED:
        ((MainApplication) getApplication()).initGeoLocation();
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
