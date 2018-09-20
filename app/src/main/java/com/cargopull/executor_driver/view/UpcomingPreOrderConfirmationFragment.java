package com.cargopull.executor_driver.view;

import com.cargopull.executor_driver.di.AppComponent;

/**
 * Отображает предстоящий предзаказ.
 */

public class UpcomingPreOrderConfirmationFragment extends SelectedPreOrderConfirmationFragment {

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }
}
