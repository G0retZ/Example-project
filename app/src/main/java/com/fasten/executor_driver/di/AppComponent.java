package com.fasten.executor_driver.di;

import com.fasten.executor_driver.application.MainActivity;
import com.fasten.executor_driver.view.auth.LoginFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

  void inject(LoginFragment loginFragment);

  void inject(MainActivity mainActivity);
}