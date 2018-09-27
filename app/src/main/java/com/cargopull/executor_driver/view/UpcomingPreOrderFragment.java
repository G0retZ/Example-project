package com.cargopull.executor_driver.view;

import com.cargopull.executor_driver.di.AppComponent;

public class UpcomingPreOrderFragment extends PreOrderFragment {

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }
}
