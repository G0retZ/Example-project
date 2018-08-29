package com.cargopull.executor_driver.di;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.interactor.CallToClientGateway;
import com.cargopull.executor_driver.interactor.CancelOrderGateway;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentGateway;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.interactor.ExecutorStateSwitchGateway;
import com.cargopull.executor_driver.interactor.GeoLocationGateway;
import com.cargopull.executor_driver.interactor.GeoTrackingGateway;
import com.cargopull.executor_driver.interactor.MissedOrderGateway;
import com.cargopull.executor_driver.interactor.MovingToClientGateway;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import com.cargopull.executor_driver.interactor.OrderCurrentCostGateway;
import com.cargopull.executor_driver.interactor.OrderGateway;
import com.cargopull.executor_driver.interactor.OrderRouteGateway;
import com.cargopull.executor_driver.interactor.ServerConnectionGateway;
import com.cargopull.executor_driver.interactor.ServerTimeGateway;
import com.cargopull.executor_driver.interactor.UpdateMessageGateway;
import com.cargopull.executor_driver.interactor.WaitingForClientGateway;
import com.cargopull.executor_driver.interactor.auth.PasswordGateway;
import com.cargopull.executor_driver.interactor.auth.SmsGateway;
import com.cargopull.executor_driver.interactor.map.HeatMapGateway;
import com.cargopull.executor_driver.interactor.services.ServicesGateway;
import com.cargopull.executor_driver.interactor.vehicle.LastUsedVehicleGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import java.util.List;

interface RepositoryComponent {

  @NonNull
  CallToClientGateway getCallToClientGateway();

  @NonNull
  CancelOrderGateway getCancelOrderGateway();

  @NonNull
  CommonGateway<List<CancelOrderReason>> getCancelOrderReasonsGateway();

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
  MissedOrderGateway getMissedOrderGateway();

  @NonNull
  MovingToClientGateway getMovingToClientGateway();

  @NonNull
  OrderConfirmationGateway getOrderConfirmationGateway();

  @NonNull
  OrderConfirmationGateway getPreOrderConfirmationGateway();

  @NonNull
  CommonGateway<OrderCostDetails> getOrderCostDetailsGateway();

  @NonNull
  OrderCurrentCostGateway getOrderCurrentCostGateway();

  @NonNull
  OrderGateway getOrderGateway();

  @NonNull
  OrderGateway getPreOrderGateway();

  @NonNull
  OrderRouteGateway getOrderRouteGateway();

  @NonNull
  ServerConnectionGateway getServerConnectionGateway();

  @NonNull
  ServerTimeGateway getServerTimeGateway();

  @NonNull
  UpdateMessageGateway getUpdateMessageGateway();

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
  VehicleOptionsGateway getVehicleOptionsGateway();

  @NonNull
  VehicleOptionsGateway getCurrentVehicleOptionsGateway();

  @NonNull
  VehiclesAndOptionsGateway getVehiclesAndOptionsGateway();

  @NonNull
  VehiclesAndOptionsGateway getSelectedVehiclesAndOptionsGateway();
}
