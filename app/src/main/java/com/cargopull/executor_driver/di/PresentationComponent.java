package com.cargopull.executor_driver.di;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModel;
import com.cargopull.executor_driver.presentation.calltooperator.CallToOperatorViewModel;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModel;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderViewModel;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModel;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import com.cargopull.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModel;
import com.cargopull.executor_driver.presentation.code.CodeViewModel;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModel;
import com.cargopull.executor_driver.presentation.confirmorderpayment.ConfirmOrderPaymentViewModel;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.cargopull.executor_driver.presentation.map.MapViewModel;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModel;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewModel;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModel;
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModel;
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModel;
import com.cargopull.executor_driver.presentation.phone.PhoneViewModel;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModel;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel;
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel;
import com.cargopull.executor_driver.presentation.services.ServicesSliderViewModel;
import com.cargopull.executor_driver.presentation.services.ServicesViewModel;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewModel;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModel;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientViewModel;

interface PresentationComponent {

  @NonNull
  AnnouncementViewModel getAnnouncementViewModel();

  @NonNull
  BalanceViewModel getBalanceViewModel();

  @NonNull
  CallToClientViewModel getCallToClientViewModel(@Nullable Fragment fragment);

  @NonNull
  CallToOperatorViewModel getCallToOperatorViewModel(@Nullable Fragment fragment);

  @NonNull
  CancelledOrderViewModel getCancelledOrderViewModel();

  @NonNull
  CancelOrderViewModel getCancelOrderViewModel(@Nullable Fragment fragment);

  @NonNull
  CancelOrderReasonsViewModel getCancelOrderReasonsViewModel();

  @NonNull
  ChooseVehicleViewModel getChooseVehicleViewModel(@Nullable Fragment fragment);

  @NonNull
  ChooseVehicleViewModel getCurrentChooseVehicleViewModel(@Nullable Fragment fragment);

  @NonNull
  ClientOrderConfirmationTimeViewModel getClientOrderConfirmationTimeViewModel(
      @Nullable Fragment fragment);

  @NonNull
  CodeViewModel getCodeViewModel(@Nullable Fragment fragment);

  @NonNull
  CodeHeaderViewModel getCodeHeaderViewModel(@Nullable Fragment fragment);

  @NonNull
  ConfirmOrderPaymentViewModel getConfirmOrderPaymentViewModel(@Nullable Fragment fragment);

  @NonNull
  CurrentCostPollingViewModel getCurrentCostPollingViewModel();

  @NonNull
  ExecutorStateViewModel getExecutorStateViewModel();

  @NonNull
  GeoLocationViewModel getGeoLocationViewModel();

  @NonNull
  MapViewModel getMapViewModel(@Nullable Fragment fragment);

  @NonNull
  MissedOrderViewModel getMissedOrderViewModel();

  @NonNull
  MovingToClientViewModel getMovingToClientViewModel(@Nullable Fragment fragment);

  @NonNull
  NextRoutePointViewModel getNextRoutePointViewModel(@Nullable Fragment fragment);

  @NonNull
  OnlineButtonViewModel getOnlineButtonViewModel(@Nullable Fragment fragment);

  @NonNull
  OnlineButtonViewModel getSelectedOnlineButtonViewModel(@Nullable Fragment fragment);

  @NonNull
  OnlineSwitchViewModel getOnlineSwitchViewModel(@Nullable Fragment fragment);

  @NonNull
  OnlineSwitchViewModel getExitOnlineSwitchViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderCostDetailsViewModel getOrderCostDetailsViewModel();

  @NonNull
  OrderViewModel getOrderViewModel();

  @NonNull
  OrderViewModel getPOrderViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderConfirmationViewModel getOrderConfirmationViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderConfirmationViewModel getPreOrderBookingViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderCostViewModel getOrderCostViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderRouteViewModel getOrderRouteViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderTimeViewModel getOrderTimeViewModel(@Nullable Fragment fragment);

  @NonNull
  PhoneViewModel getPhoneViewModel(@Nullable Fragment fragment);

  @NonNull
  PreOrderViewModel getPreOrderViewModel();

  @NonNull
  PreOrdersListViewModel getPreOrdersListViewModel();

  @NonNull
  SelectedVehicleViewModel getSelectedVehicleViewModel(@Nullable Fragment fragment);

  @NonNull
  ServerConnectionViewModel getServerConnectionViewModel();

  @NonNull
  ServerTimeViewModel getServerTimeViewModel();

  @NonNull
  ServicesSliderViewModel getServicesSliderViewModel();

  @NonNull
  ServicesViewModel getServicesViewModel(@Nullable Fragment fragment);

  @NonNull
  SmsButtonViewModel getSmsButtonViewModel(@Nullable Fragment fragment);

  @NonNull
  UpcomingPreOrderMessageViewModel getUpcomingPreOrderMessagesViewModel();

  @NonNull
  UpdateMessageViewModel getUpdateMessageViewModel();

  @NonNull
  VehicleOptionsViewModel getVehicleOptionsViewModel(@Nullable Fragment fragment);

  @NonNull
  VehicleOptionsViewModel getSelectedVehicleOptionsViewModel(@Nullable Fragment fragment);

  @NonNull
  WaitingForClientViewModel getWaitingForClientViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderViewModel getSelectedPreOrderViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderConfirmationViewModel getSelectedPreOrderConfirmationViewModel(@Nullable Fragment fragment);

  @NonNull
  OrderViewModel getUpcomingPreOrderViewModel();

  @NonNull
  OrderConfirmationViewModel getUpcomingPreOrderConfirmationViewModel(@Nullable Fragment fragment);
}
