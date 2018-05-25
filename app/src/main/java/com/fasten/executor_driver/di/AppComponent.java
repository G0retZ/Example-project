package com.fasten.executor_driver.di;

import com.fasten.executor_driver.application.MainApplication;
import com.fasten.executor_driver.view.ChooseVehicleFragment;
import com.fasten.executor_driver.view.ClientOrderConfirmationFragment;
import com.fasten.executor_driver.view.DriverOrderConfirmationFragment;
import com.fasten.executor_driver.view.GoOnlineFragment;
import com.fasten.executor_driver.view.MapFragment;
import com.fasten.executor_driver.view.MovingToClientFragment;
import com.fasten.executor_driver.view.OnlineFragment;
import com.fasten.executor_driver.view.OrderFulfillmentFragment;
import com.fasten.executor_driver.view.SelectedVehicleFragment;
import com.fasten.executor_driver.view.SelectedVehicleOptionsFragment;
import com.fasten.executor_driver.view.ServicesFragment;
import com.fasten.executor_driver.view.VehicleOptionsFragment;
import com.fasten.executor_driver.view.WaitingForClientFragment;
import com.fasten.executor_driver.view.auth.LoginFragment;
import com.fasten.executor_driver.view.auth.PasswordFragment;

public interface AppComponent {

  void inject(MainApplication mainApplication);

  void inject(LoginFragment loginFragment);

  void inject(PasswordFragment passwordFragment);

  void inject(MapFragment mapFragment);

  void inject(OnlineFragment onlineFragment);

  void inject(GoOnlineFragment goOnlineFragment);

  void inject(ChooseVehicleFragment chooseVehicleFragment);

  void inject(VehicleOptionsFragment vehicleOptionsFragment);

  void inject(SelectedVehicleOptionsFragment vehicleOptionsFragment);

  void inject(SelectedVehicleFragment selectedVehicleFragment);

  void inject(ServicesFragment servicesFragment);

  void inject(DriverOrderConfirmationFragment driverOrderConfirmationFragment);

  void inject(ClientOrderConfirmationFragment clientOrderConfirmationFragment);

  void inject(MovingToClientFragment movingToClientFragment);

  void inject(WaitingForClientFragment waitingForClientFragment);

  void inject(OrderFulfillmentFragment orderFulfillmentFragment);
}