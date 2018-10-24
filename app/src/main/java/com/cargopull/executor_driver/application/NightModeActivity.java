package com.cargopull.executor_driver.application;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.di.AppComponent;
import javax.inject.Inject;

public class NightModeActivity extends BaseActivity {

  private AppSettingsService appSettingsService;
  private View nightModeAutoSelector;
  private View nightModeOffSelector;
  private View nightModeOnSelector;
  private TextView nightModeAuto;
  private TextView nightModeOff;
  private TextView nightModeOn;

  @Inject
  public void setAppSettingsService(@NonNull AppSettingsService appSettingsService) {
    this.appSettingsService = appSettingsService;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_night_mode);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    nightModeAutoSelector = findViewById(R.id.selectorNightModeAuto);
    nightModeOffSelector = findViewById(R.id.selectorNightModeOff);
    nightModeOnSelector = findViewById(R.id.selectorNightModeOn);
    nightModeAuto = findViewById(R.id.nightModeAuto);
    nightModeOff = findViewById(R.id.nightModeOff);
    nightModeOn = findViewById(R.id.nightModeOn);
    nightModeAuto.setOnClickListener(v -> {
      switchSelection(AppCompatDelegate.MODE_NIGHT_AUTO);
      appSettingsService.saveNumber("mode", AppCompatDelegate.MODE_NIGHT_AUTO);
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
      onBackPressed();
    });
    nightModeOff.setOnClickListener(v -> {
      switchSelection(AppCompatDelegate.MODE_NIGHT_NO);
      appSettingsService.saveNumber("mode", AppCompatDelegate.MODE_NIGHT_NO);
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      onBackPressed();
    });
    nightModeOn.setOnClickListener(v -> {
      switchSelection(AppCompatDelegate.MODE_NIGHT_YES);
      appSettingsService.saveNumber("mode", AppCompatDelegate.MODE_NIGHT_YES);
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      onBackPressed();
    });
    switchSelection(appSettingsService.getNumber("mode"));
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    super.onDependencyInject(appComponent);
    appComponent.inject(this);
  }

  private void switchSelection(int value) {
    switch (value) {
      case AppCompatDelegate.MODE_NIGHT_YES:
        nightModeAuto.setEnabled(true);
        nightModeAutoSelector.setVisibility(View.GONE);
        nightModeOff.setEnabled(true);
        nightModeOffSelector.setVisibility(View.GONE);
        nightModeOn.setEnabled(false);
        nightModeOnSelector.setVisibility(View.VISIBLE);
        break;
      case AppCompatDelegate.MODE_NIGHT_NO:
        nightModeAuto.setEnabled(true);
        nightModeAutoSelector.setVisibility(View.GONE);
        nightModeOff.setEnabled(false);
        nightModeOffSelector.setVisibility(View.VISIBLE);
        nightModeOn.setEnabled(true);
        nightModeOnSelector.setVisibility(View.GONE);
        break;
      default:
        nightModeAuto.setEnabled(false);
        nightModeAutoSelector.setVisibility(View.VISIBLE);
        nightModeOff.setEnabled(true);
        nightModeOffSelector.setVisibility(View.GONE);
        nightModeOn.setEnabled(true);
        nightModeOnSelector.setVisibility(View.GONE);
    }
  }
}
