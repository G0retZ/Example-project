package com.cargopull.executor_driver.di;

import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.FcmService;
import com.cargopull.executor_driver.application.MainApplication;
import com.cargopull.executor_driver.application.MenuActivity;
import com.cargopull.executor_driver.application.MovingToClientActivity;
import com.cargopull.executor_driver.application.NightModeActivity;
import com.cargopull.executor_driver.application.OnlineActivity;
import com.cargopull.executor_driver.application.OnlineMenuActivity;
import com.cargopull.executor_driver.application.OrderCostDetailsActivity;
import com.cargopull.executor_driver.application.OrderFulfillmentActivity;
import com.cargopull.executor_driver.application.PasswordActivity;
import com.cargopull.executor_driver.application.PreOrdersActivity;
import com.cargopull.executor_driver.application.WaitingForClientActivity;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.utils.Consumer;
import com.cargopull.executor_driver.view.ActionOrderDetailsFragment;
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
import com.cargopull.executor_driver.view.DriverPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.GeoEngagementDialogFragment;
import com.cargopull.executor_driver.view.GoOnlineFragment;
import com.cargopull.executor_driver.view.MapFragment;
import com.cargopull.executor_driver.view.MenuFragment;
import com.cargopull.executor_driver.view.MovingToClientActionsDialogFragment;
import com.cargopull.executor_driver.view.MovingToClientFragment;
import com.cargopull.executor_driver.view.MovingToClientRouteFragment;
import com.cargopull.executor_driver.view.NewPreOrderButtonFragment;
import com.cargopull.executor_driver.view.NewPreOrderFragment;
import com.cargopull.executor_driver.view.OnlineFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsRouteFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentFragment;
import com.cargopull.executor_driver.view.OrderRouteFragment;
import com.cargopull.executor_driver.view.OrdersHistoryHeaderFragment;
import com.cargopull.executor_driver.view.PreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.PreOrderFragment;
import com.cargopull.executor_driver.view.PreOrdersFragment;
import com.cargopull.executor_driver.view.ProfileFragment;
import com.cargopull.executor_driver.view.SelectedPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.SelectedPreOrderFragment;
import com.cargopull.executor_driver.view.SelectedVehicleFragment;
import com.cargopull.executor_driver.view.SelectedVehicleOptionsFragment;
import com.cargopull.executor_driver.view.ServerConnectionFragment;
import com.cargopull.executor_driver.view.UpcomingPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.UpcomingPreOrderFragment;
import com.cargopull.executor_driver.view.UpcomingPreOrderNotificationFragment;
import com.cargopull.executor_driver.view.VehicleOptionsFragment;
import com.cargopull.executor_driver.view.WaitingForClientActionsDialogFragment;
import com.cargopull.executor_driver.view.WaitingForClientFragment;
import com.cargopull.executor_driver.view.WaitingForClientRouteFragment;
import com.cargopull.executor_driver.view.auth.LoginFragment;
import com.cargopull.executor_driver.view.auth.PasswordFragment;

public interface AppComponent {

  void inject(Consumer<AppSettingsService> appSettingsServiceConsumer);

  void inject(MainApplication mainApplication);

  void inject(BaseActivity baseActivity);

  void inject(MovingToClientActivity movingToClientActivity);

  void inject(WaitingForClientActivity waitingForClientActivity);

  void inject(OrderFulfillmentActivity orderFulfillmentActivity);

  void inject(OrderCostDetailsActivity orderCostDetailsActivity);

  void inject(MenuActivity menuActivity);

  void inject(OnlineMenuActivity onlineMenuActivity);

  void inject(NightModeActivity nightModeActivity);

  void inject(OnlineActivity onlineActivity);

  void inject(PreOrdersActivity preOrdersActivity);

  void inject(PasswordActivity passwordActivity);

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

  void inject(DriverOrderConfirmationFragment driverOrderConfirmationFragment);

  void inject(ClientOrderConfirmationFragment clientOrderConfirmationFragment);

  void inject(ClientOrderConfirmationTimeFragment clientOrderConfirmationTimeFragment);

  void inject(MovingToClientFragment movingToClientFragment);

  void inject(MovingToClientActionsDialogFragment movingToClientActionsDialogFragment);

  void inject(ActionOrderDetailsFragment actionOrderDetailsFragment);

  void inject(MovingToClientRouteFragment movingToClientRouteFragment);

  void inject(WaitingForClientFragment waitingForClientFragment);

  void inject(WaitingForClientActionsDialogFragment waitingForClientActionsDialogFragment);

  void inject(WaitingForClientRouteFragment waitingForClientRouteFragment);

  void inject(OrderFulfillmentFragment orderFulfillmentFragment);

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

  void inject(OrderCostDetailsRouteFragment orderCostDetailsRouteFragment);

  void inject(OrderCostDetailsActionsDialogFragment orderCostDetailsActionsDialogFragment);

  void inject(ProfileFragment profileFragment);

  void inject(DriverPreOrderBookingFragment driverPreOrderBookingFragment);

  void inject(PreOrdersFragment preOrdersFragment);

  void inject(PreOrderFragment preOrderFragment);

  void inject(SelectedPreOrderFragment selectedPreOrderFragment);

  void inject(SelectedPreOrderConfirmationFragment selectedPreOrderConfirmationFragment);

  void inject(NewPreOrderFragment newPreOrderFragment);

  void inject(NewPreOrderButtonFragment newPreOrderButtonFragment);

  void inject(DriverPreOrderConfirmationFragment driverPreOrderConfirmationFragment);

  void inject(PreOrderConfirmationFragment preOrderConfirmationFragment);

  void inject(UpcomingPreOrderFragment upcomingPreOrderFragment);

  void inject(UpcomingPreOrderConfirmationFragment upcomingPreOrderConfirmationFragment);

  void inject(UpcomingPreOrderNotificationFragment upcomingPreOrderNotificationFragment);

  void inject(GeoEngagementDialogFragment geoEngagementDialogFragment);

  void inject(OrdersHistoryHeaderFragment ordersHistoryHeaderFragment, int offset);
}
