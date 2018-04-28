package com.fasten.executor_driver.view;

import com.fasten.executor_driver.di.AppComponent;

/**
 * Отображает список ТС для выбора при переходе в онлайн.
 */

public class SelectedVehicleOptionsFragment extends VehicleOptionsFragment {

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }
}
