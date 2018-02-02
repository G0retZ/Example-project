package com.fasten.executor_driver.di;

import com.fasten.executor_driver.application.MainActivity;
import com.fasten.executor_driver.view.auth.LoginFragment;
import com.fasten.executor_driver.view.auth.PasswordFragment;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    AppModule.class,
    GatewayModule.class,
    EntityModule.class,
    UseCaseModule.class,
    PresentationModule.class
})
public interface AppComponent {

  void inject(MainActivity mainActivity);

  void inject(LoginFragment loginFragment);

  void inject(PasswordFragment loginFragment);
}