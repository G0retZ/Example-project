package com.cargopull.executor_driver.di;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.CallToClientUseCase;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCase;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentUseCase;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCase;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.GeoLocationUseCase;
import com.cargopull.executor_driver.interactor.MissedOrderUseCase;
import com.cargopull.executor_driver.interactor.MovingToClientUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.interactor.OrderCostDetailsUseCase;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCase;
import com.cargopull.executor_driver.interactor.OrderFulfillmentTimeUseCase;
import com.cargopull.executor_driver.interactor.OrderRouteUseCase;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.interactor.OrdersUseCase;
import com.cargopull.executor_driver.interactor.SelectedOrderUseCase;
import com.cargopull.executor_driver.interactor.ServerConnectionUseCase;
import com.cargopull.executor_driver.interactor.ServerTimeUseCase;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCase;
import com.cargopull.executor_driver.interactor.WaitingForClientUseCase;
import com.cargopull.executor_driver.interactor.auth.LoginUseCase;
import com.cargopull.executor_driver.interactor.auth.PasswordUseCase;
import com.cargopull.executor_driver.interactor.auth.SmsUseCase;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCase;
import com.cargopull.executor_driver.interactor.services.ServicesUseCase;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;

interface InteractorComponent {

  @NonNull
  CallToClientUseCase getCallToClientUseCase();

  @NonNull
  CancelOrderReasonsUseCase getCancelOrderReasonsUseCase();

  @NonNull
  CancelOrderUseCase getCancelOrderUseCase();

  @NonNull
  ConfirmOrderPaymentUseCase getConfirmOrderPaymentUseCase();

  @NonNull
  CurrentCostPollingUseCase getCurrentCostPollingUseCase();

  @NonNull
  ExecutorBalanceUseCase getExecutorBalanceUseCase();

  @NonNull
  ExecutorStateNotOnlineUseCase getExecutorStateNotOnlineUseCase();

  @NonNull
  ExecutorStateNotOnlineUseCase getExecutorStateExitUseCase();

  @NonNull
  ExecutorStateUseCase getExecutorStateUseCase();

  @NonNull
  GeoLocationUseCase getGeoLocationUseCase();

  @NonNull
  MissedOrderUseCase getMissedOrderUseCase();

  @NonNull
  MovingToClientUseCase getMovingToClientUseCase();

  @NonNull
  OrderConfirmationUseCase getOrderConfirmationUseCase();

  @NonNull
  OrderConfirmationUseCase getPreOrderConfirmationUseCase();

  @NonNull
  OrderCostDetailsUseCase getOrderCostDetailsUseCase();

  @NonNull
  OrderCurrentCostUseCase getOrderCurrentCostUseCase();

  @NonNull
  OrderFulfillmentTimeUseCase getOrderFulfillmentTimeUseCase();

  @NonNull
  OrderRouteUseCase getOrderRouteUseCase();

  @NonNull
  OrderUseCase getOrderUseCase();

  @NonNull
  OrderUseCase getPreOrderUseCase();

  @NonNull
  ServerConnectionUseCase getServerConnectionUseCase();

  @NonNull
  ServerTimeUseCase getServerTimeUseCase();

  @NonNull
  UpdateMessageUseCase getUpdateMessageUseCase();

  @NonNull
  WaitingForClientUseCase getWaitingForClientUseCase();

  @NonNull
  LoginUseCase getLoginUseCase();

  @NonNull
  OrdersUseCase getPreOrdersListUseCase();

  @NonNull
  PasswordUseCase getPasswordUseCase();

  @NonNull
  SmsUseCase getSmsUseCase();

  @NonNull
  HeatMapUseCase getHeatMapUseCase();

  @NonNull
  ServicesUseCase getServicesUseCase();

  @NonNull
  SelectedVehicleUseCase getSelectedVehicleUseCase();

  @NonNull
  VehicleChoiceUseCase getVehicleChoiceUseCase();

  @NonNull
  VehicleChoiceUseCase getCurrentVehicleChoiceUseCase();

  @NonNull
  VehicleOptionsUseCase getVehicleOptionsUseCase();

  @NonNull
  VehicleOptionsUseCase getCurrentVehicleOptionsUseCase();

  @NonNull
  VehiclesAndOptionsUseCase getVehiclesAndOptionsUseCase();

  @NonNull
  VehiclesAndOptionsUseCase getSelectedVehiclesAndOptionsUseCase();

  @NonNull
  SelectedOrderUseCase getSelectedOrderUseCase();

  @NonNull
  OrderUseCase getSelectedPreOrderUseCase();
}
