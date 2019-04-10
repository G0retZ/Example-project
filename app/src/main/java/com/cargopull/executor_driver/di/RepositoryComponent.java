package com.cargopull.executor_driver.di;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.CallToClientGateway;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentGateway;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.cargopull.executor_driver.interactor.DataUpdateAndResetUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateSwitchGateway;
import com.cargopull.executor_driver.interactor.GeoLocationGateway;
import com.cargopull.executor_driver.interactor.GeoTrackingGateway;
import com.cargopull.executor_driver.interactor.MovingToClientGateway;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import com.cargopull.executor_driver.interactor.OrderRouteGateway;
import com.cargopull.executor_driver.interactor.OrdersHistorySummaryGateway;
import com.cargopull.executor_driver.interactor.ReportProblemGateway;
import com.cargopull.executor_driver.interactor.ServerConnectionGateway;
import com.cargopull.executor_driver.interactor.WaitingForClientGateway;
import com.cargopull.executor_driver.interactor.auth.PasswordGateway;
import com.cargopull.executor_driver.interactor.auth.SmsGateway;
import com.cargopull.executor_driver.interactor.map.HeatMapGateway;
import com.cargopull.executor_driver.interactor.services.ServicesGateway;
import com.cargopull.executor_driver.interactor.vehicle.LastUsedVehicleGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import java.util.Set;

interface RepositoryComponent {

  @NonNull
  DataReceiver<String> getLoginGateway();

  @NonNull
  DataUpdateAndResetUseCase<String> getLoginStorage();

  @NonNull
  CallToClientGateway getCallToClientGateway();

  @NonNull
  CommonGateway<Order> getCancelledOrderGateway();

  @NonNull
  CommonGateway<String> getCancelledOrderMessageGateway();

  @NonNull
  ReportProblemGateway getReportProblemGateway();

  @NonNull
  ConfirmOrderPaymentGateway getConfirmOrderPaymentGateway();

  @NonNull
  CurrentCostPollingGateway getCurrentCostPollingGateway();

  @NonNull
  CommonGateway<ExecutorBalance> getExecutorBalanceGateway();

  @NonNull
  CommonGateway<ExecutorState> getExecutorStateGateway();

  @NonNull
  ExecutorStateSwitchGateway getExecutorStateSwitchGateway();

  @NonNull
  GeoLocationGateway getGeoLocationGateway();

  @NonNull
  GeoTrackingGateway getGeoTrackingGateway();

  @NonNull
  CommonGateway<String> getMissedOrderGateway();

  @NonNull
  MovingToClientGateway getMovingToClientGateway();

  @NonNull
  OrderConfirmationGateway getOrderConfirmationGateway();

  @NonNull
  OrderConfirmationGateway getPreOrderConfirmationGateway();

  @NonNull
  OrderConfirmationGateway getPreOrderProcessGateway();

  @NonNull
  CommonGateway<Long> getOrderCurrentCostGateway();

  @NonNull
  CommonGateway<Order> getOrderGateway();

  @NonNull
  CommonGateway<Order> getPreOrderGateway();

  @NonNull
  OrderRouteGateway getOrderRouteGateway();

  @NonNull
  ServerConnectionGateway getServerConnectionGateway();

  @NonNull
  CommonGateway<Long> getServerTimeGateway();

  @NonNull
  CommonGateway<String> getUpdateMessageGateway();

  @NonNull
  WaitingForClientGateway getWaitingForClientGateway();

  @NonNull
  PasswordGateway getPasswordGateway();

  @NonNull
  SmsGateway getSmsGateway();

  @NonNull
  HeatMapGateway getHeatMapGateway();

  @NonNull
  ServicesGateway getServicesGateway();

  @NonNull
  LastUsedVehicleGateway getLastUsedVehicleGateway();

  @NonNull
  CommonGateway<String> getUpcomingPreOrderMessagesGateway();

  @NonNull
  VehicleOptionsGateway getVehicleOptionsGateway();

  @NonNull
  VehicleOptionsGateway getCurrentVehicleOptionsGateway();

  @NonNull
  VehiclesAndOptionsGateway getVehiclesAndOptionsGateway();

  @NonNull
  VehiclesAndOptionsGateway getSelectedVehiclesAndOptionsGateway();

  @NonNull
  CommonGateway<Set<Order>> getPreOrdersSetGateway();

  @NonNull
  CommonGateway<Order> getUpcomingPreOrderGateway();

  @NonNull
  CommonGateway<Boolean> getGeoLocationStateGateway();

  @NonNull
  CommonGateway<String> getAnnouncementsGateway();

  @NonNull
  OrdersHistorySummaryGateway getOrdersHistorySummaryGateway();
}
