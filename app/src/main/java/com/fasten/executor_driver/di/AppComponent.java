package com.fasten.executor_driver.di;

import com.fasten.executor_driver.view.ChooseVehicleFragment;
import com.fasten.executor_driver.view.GoOnlineFragment;
import com.fasten.executor_driver.view.MapFragment;
import com.fasten.executor_driver.view.SelectedVehicleFragment;
import com.fasten.executor_driver.view.VehicleOptionsFragment;
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

  void inject(LoginFragment loginFragment);

  void inject(PasswordFragment passwordFragment);

  void inject(MapFragment mapFragment);

  void inject(GoOnlineFragment goOnlineFragment);

  void inject(ChooseVehicleFragment chooseVehicleFragment);

  void inject(VehicleOptionsFragment vehicleOptionsFragment);

  void inject(SelectedVehicleFragment selectedVehicleFragment);
}