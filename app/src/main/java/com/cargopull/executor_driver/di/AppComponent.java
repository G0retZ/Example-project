package com.cargopull.executor_driver.di;

import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.FcmService;
import com.cargopull.executor_driver.application.MainApplication;
import com.cargopull.executor_driver.view.BalanceFragment;
import com.cargopull.executor_driver.view.BalanceSummaryFragment;
import com.cargopull.executor_driver.view.CallToClientFragment;
import com.cargopull.executor_driver.view.CallToOperatorFragment;
import com.cargopull.executor_driver.view.CancelOrderDialogFragment;
import com.cargopull.executor_driver.view.ChooseVehicleFragment;
import com.cargopull.executor_driver.view.ClientOrderConfirmationFragment;
import com.cargopull.executor_driver.view.ClientOrderConfirmationTimeFragment;
import com.cargopull.executor_driver.view.DriverOrderConfirmationFragment;
import com.cargopull.executor_driver.view.DriverPreOrderBookingFragment;
import com.cargopull.executor_driver.view.GoOnlineFragment;
import com.cargopull.executor_driver.view.MapFragment;
import com.cargopull.executor_driver.view.MenuFragment;
import com.cargopull.executor_driver.view.MovingToClientDetailsFragment;
import com.cargopull.executor_driver.view.MovingToClientFragment;
import com.cargopull.executor_driver.view.MovingToClientRouteFragment;
import com.cargopull.executor_driver.view.OnlineFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsOrderDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsRouteFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentDetailsFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentFragment;
import com.cargopull.executor_driver.view.OrderRouteFragment;
import com.cargopull.executor_driver.view.PreOrderFragment;
import com.cargopull.executor_driver.view.PreOrdersFragment;
import com.cargopull.executor_driver.view.ProfileFragment;
import com.cargopull.executor_driver.view.SelectedVehicleFragment;
import com.cargopull.executor_driver.view.SelectedVehicleOptionsFragment;
import com.cargopull.executor_driver.view.ServerConnectionFragment;
import com.cargopull.executor_driver.view.ServicesFragment;
import com.cargopull.executor_driver.view.VehicleOptionsFragment;
import com.cargopull.executor_driver.view.WaitingForClientFragment;
import com.cargopull.executor_driver.view.WaitingForClientRouteFragment;
import com.cargopull.executor_driver.view.auth.LoginFragment;
import com.cargopull.executor_driver.view.auth.PasswordFragment;

public interface AppComponent {

  void inject(MainApplication mainApplication);

  void inject(BaseActivity baseActivity);

  void inject(FcmService fcmService);

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

  void inject(ClientOrderConfirmationTimeFragment clientOrderConfirmationTimeFragment);

  void inject(MovingToClientFragment movingToClientFragment);

  void inject(MovingToClientDetailsFragment movingToClientDetailsFragment);

  void inject(MovingToClientRouteFragment movingToClientRouteFragment);

  void inject(WaitingForClientFragment waitingForClientFragment);

  void inject(WaitingForClientRouteFragment waitingForClientRouteFragment);

  void inject(OrderFulfillmentFragment orderFulfillmentFragment);

  void inject(OrderFulfillmentDetailsFragment orderFulfillmentDetailsFragment);

  void inject(OrderFulfillmentActionsDialogFragment orderFulfillmentActionsDialogFragment);

  void inject(OrderRouteFragment orderRouteFragment);

  void inject(CallToClientFragment callToClientFragment);

  void inject(CallToOperatorFragment callToOperatorFragment);

  void inject(CancelOrderDialogFragment cancelOrderDialogFragment);

  void inject(BalanceFragment balanceFragment);

  void inject(BalanceSummaryFragment balanceSummaryFragment);

  void inject(MenuFragment menuFragment);

  void inject(ServerConnectionFragment serverConnectionFragment);

  void inject(OrderCostDetailsFragment orderCostDetailsFragment);

  void inject(OrderCostDetailsOrderDetailsFragment orderCostDetailsOrderDetailsFragment);

  void inject(OrderCostDetailsRouteFragment orderCostDetailsRouteFragment);

  void inject(ProfileFragment profileFragment);

  void inject(DriverPreOrderBookingFragment driverPreOrderBookingFragment);

  void inject(PreOrdersFragment preOrdersFragment);

  void inject(PreOrderFragment preOrderFragment);
}
