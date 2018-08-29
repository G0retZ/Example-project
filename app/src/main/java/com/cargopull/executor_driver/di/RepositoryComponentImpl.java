package com.cargopull.executor_driver.di;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.CallToClientGatewayImpl;
import com.cargopull.executor_driver.gateway.CancelOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.CancelOrderReasonApiMapper;
import com.cargopull.executor_driver.gateway.CancelOrderReasonsGatewayImpl;
import com.cargopull.executor_driver.gateway.ConfirmOrderPaymentGatewayImpl;
import com.cargopull.executor_driver.gateway.CurrentCostPollingGatewayImpl;
import com.cargopull.executor_driver.gateway.CurrentCostPollingTimersApiMapper;
import com.cargopull.executor_driver.gateway.CurrentVehicleOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.ErrorMapper;
import com.cargopull.executor_driver.gateway.ExecutorBalanceApiMapper;
import com.cargopull.executor_driver.gateway.ExecutorBalanceGatewayImpl;
import com.cargopull.executor_driver.gateway.ExecutorStateApiMapper;
import com.cargopull.executor_driver.gateway.ExecutorStateGatewayImpl;
import com.cargopull.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import com.cargopull.executor_driver.gateway.GeoLocationGatewayImpl;
import com.cargopull.executor_driver.gateway.GeoTrackingGatewayImpl;
import com.cargopull.executor_driver.gateway.HeatMapGatewayImpl;
import com.cargopull.executor_driver.gateway.LastUsedVehicleGatewayImpl;
import com.cargopull.executor_driver.gateway.MissedOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.MovingToClientGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderApiMapper;
import com.cargopull.executor_driver.gateway.OrderConfirmationGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderCostDetailsApiMapper;
import com.cargopull.executor_driver.gateway.OrderCostDetailsFilter;
import com.cargopull.executor_driver.gateway.OrderCurrentCostApiMapper;
import com.cargopull.executor_driver.gateway.OrderCurrentCostGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderRouteGatewayImpl;
import com.cargopull.executor_driver.gateway.PasswordGatewayImpl;
import com.cargopull.executor_driver.gateway.PreOrderConfirmationGatewayImpl;
import com.cargopull.executor_driver.gateway.PreOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.RoutePointApiMapper;
import com.cargopull.executor_driver.gateway.SelectedVehicleAndOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.ServerConnectionGatewayImpl;
import com.cargopull.executor_driver.gateway.ServerTimeGatewayImpl;
import com.cargopull.executor_driver.gateway.ServiceApiMapper;
import com.cargopull.executor_driver.gateway.ServicesGatewayImpl;
import com.cargopull.executor_driver.gateway.SmsGatewayImpl;
import com.cargopull.executor_driver.gateway.TopicGatewayImpl;
import com.cargopull.executor_driver.gateway.UpdateMessageGatewayImpl;
import com.cargopull.executor_driver.gateway.VehicleApiMapper;
import com.cargopull.executor_driver.gateway.VehicleOptionApiMapper;
import com.cargopull.executor_driver.gateway.VehicleOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.VehiclesAndOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.WaitingForClientGatewayImpl;
import com.cargopull.executor_driver.interactor.CallToClientGateway;
import com.cargopull.executor_driver.interactor.CancelOrderGateway;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsGateway;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentGateway;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.interactor.ExecutorBalanceGateway;
import com.cargopull.executor_driver.interactor.ExecutorStateGateway;
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

class RepositoryComponentImpl implements RepositoryComponent {

  @NonNull
  private final BackendComponent backendComponent;
  @NonNull
  private final GeolocationCenter geolocationCenter;
  @Nullable
  private CallToClientGateway callToClientGateway;
  @Nullable
  private CancelOrderGateway cancelOrderGateway;
  @Nullable
  private CancelOrderReasonsGateway cancelOrderReasonsGateway;
  @Nullable
  private ConfirmOrderPaymentGateway confirmOrderPaymentGateway;
  @Nullable
  private CurrentCostPollingGateway currentCostPollingGateway;
  @Nullable
  private ExecutorBalanceGateway executorBalanceGateway;
  @Nullable
  private ExecutorStateGateway executorStateGateway;
  @Nullable
  private ExecutorStateSwitchGateway executorStateSwitchGateway;
  @Nullable
  private GeoLocationGateway geoLocationGateway;
  @Nullable
  private GeoTrackingGateway geoTrackingGateway;
  @Nullable
  private MissedOrderGateway missedOrderGateway;
  @Nullable
  private MovingToClientGateway movingToClientGateway;
  @Nullable
  private OrderConfirmationGateway orderConfirmationGateway;
  @Nullable
  private OrderConfirmationGateway preOrderConfirmationGateway;
  @Nullable
  private CommonGateway<OrderCostDetails> orderCostDetailsGateway;
  @Nullable
  private OrderCurrentCostGateway orderCurrentCostGateway;
  @Nullable
  private OrderGateway orderGateway;
  @Nullable
  private OrderGateway preOrderGateway;
  @Nullable
  private OrderRouteGateway orderRouteGateway;
  @Nullable
  private ServerConnectionGateway serverConnectionGateway;
  @Nullable
  private ServerTimeGateway serverTimeGateway;
  @Nullable
  private UpdateMessageGateway updateMessageGateway;
  @Nullable
  private WaitingForClientGateway waitingForClientGateway;
  @Nullable
  private PasswordGateway passwordGateway;
  @Nullable
  private SmsGateway smsGateway;
  @Nullable
  private HeatMapGateway heatMapGateway;
  @Nullable
  private ServicesGateway servicesGateway;
  @Nullable
  private LastUsedVehicleGateway lastUsedVehicleGateway;
  @Nullable
  private VehicleOptionsGateway vehicleOptionsGateway;
  @Nullable
  private VehicleOptionsGateway currentVehicleOptionsGateway;
  @Nullable
  private VehiclesAndOptionsGateway vehiclesAndOptionsGateway;
  @Nullable
  private VehiclesAndOptionsGateway selectedVehiclesAndOptionsGateway;

  RepositoryComponentImpl(@NonNull BackendComponent backendComponent,
      @NonNull GeolocationCenter geolocationCenter) {
    this.backendComponent = backendComponent;
    this.geolocationCenter = geolocationCenter;
  }

  @NonNull
  @Override
  public CallToClientGateway getCallToClientGateway() {
    if (callToClientGateway == null) {
      callToClientGateway = new CallToClientGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return callToClientGateway;
  }

  @NonNull
  @Override
  public CancelOrderGateway getCancelOrderGateway() {
    if (cancelOrderGateway == null) {
      cancelOrderGateway = new CancelOrderGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return cancelOrderGateway;
  }

  @NonNull
  @Override
  public CancelOrderReasonsGateway getCancelOrderReasonsGateway() {
    if (cancelOrderReasonsGateway == null) {
      cancelOrderReasonsGateway = new CancelOrderReasonsGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          new CancelOrderReasonApiMapper()
      );
    }
    return cancelOrderReasonsGateway;
  }

  @NonNull
  @Override
  public ConfirmOrderPaymentGateway getConfirmOrderPaymentGateway() {
    if (confirmOrderPaymentGateway == null) {
      confirmOrderPaymentGateway = new ConfirmOrderPaymentGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return confirmOrderPaymentGateway;
  }

  @NonNull
  @Override
  public CurrentCostPollingGateway getCurrentCostPollingGateway() {
    if (currentCostPollingGateway == null) {
      currentCostPollingGateway = new CurrentCostPollingGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          backendComponent.getStompClient(),
          new CurrentCostPollingTimersApiMapper()
      );
    }
    return currentCostPollingGateway;
  }

  @NonNull
  @Override
  public ExecutorBalanceGateway getExecutorBalanceGateway() {
    if (executorBalanceGateway == null) {
      executorBalanceGateway = new ExecutorBalanceGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          new ExecutorBalanceApiMapper()
      );
    }
    return executorBalanceGateway;
  }

  @NonNull
  @Override
  public ExecutorStateGateway getExecutorStateGateway() {
    if (executorStateGateway == null) {
      executorStateGateway = new ExecutorStateGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          new ExecutorStateApiMapper()
      );
    }
    return executorStateGateway;
  }

  @NonNull
  @Override
  public ExecutorStateSwitchGateway getExecutorStateSwitchGateway() {
    if (executorStateSwitchGateway == null) {
      executorStateSwitchGateway = new ExecutorStateSwitchGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return executorStateSwitchGateway;
  }

  @NonNull
  @Override
  public GeoLocationGateway getGeoLocationGateway() {
    if (geoLocationGateway == null) {
      geoLocationGateway = new GeoLocationGatewayImpl(
          geolocationCenter
      );
    }
    return geoLocationGateway;
  }

  @NonNull
  @Override
  public GeoTrackingGateway getGeoTrackingGateway() {
    if (geoTrackingGateway == null) {
      geoTrackingGateway = new GeoTrackingGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return geoTrackingGateway;
  }

  @NonNull
  @Override
  public MissedOrderGateway getMissedOrderGateway() {
    if (missedOrderGateway == null) {
      missedOrderGateway = new MissedOrderGatewayImpl(
          backendComponent.getPersonalTopicListener()
      );
    }
    return missedOrderGateway;
  }

  @NonNull
  @Override
  public MovingToClientGateway getMovingToClientGateway() {
    if (movingToClientGateway == null) {
      movingToClientGateway = new MovingToClientGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return movingToClientGateway;
  }

  @NonNull
  @Override
  public OrderConfirmationGateway getOrderConfirmationGateway() {
    if (orderConfirmationGateway == null) {
      orderConfirmationGateway = new OrderConfirmationGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return orderConfirmationGateway;
  }

  @NonNull
  @Override
  public OrderConfirmationGateway getPreOrderConfirmationGateway() {
    if (preOrderConfirmationGateway == null) {
      preOrderConfirmationGateway = new PreOrderConfirmationGatewayImpl(
          backendComponent.getApiService()
      );
    }
    return preOrderConfirmationGateway;
  }

  @NonNull
  @Override
  public CommonGateway<OrderCostDetails> getOrderCostDetailsGateway() {
    if (orderCostDetailsGateway == null) {
      orderCostDetailsGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new OrderCostDetailsApiMapper(),
          new OrderCostDetailsFilter()
      );
    }
    return orderCostDetailsGateway;
  }

  @NonNull
  @Override
  public OrderCurrentCostGateway getOrderCurrentCostGateway() {
    if (orderCurrentCostGateway == null) {
      orderCurrentCostGateway = new OrderCurrentCostGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          new OrderCurrentCostApiMapper()
      );
    }
    return orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public OrderGateway getOrderGateway() {
    if (orderGateway == null) {
      orderGateway = new OrderGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          new OrderApiMapper(
              new VehicleOptionApiMapper(),
              new RoutePointApiMapper()
          )
      );
    }
    return orderGateway;
  }

  @NonNull
  @Override
  public OrderGateway getPreOrderGateway() {
    if (preOrderGateway == null) {
      preOrderGateway = new PreOrderGatewayImpl(
          backendComponent.getPersonalTopicListener(),
          new OrderApiMapper(
              new VehicleOptionApiMapper(),
              new RoutePointApiMapper()
          )
      );
    }
    return preOrderGateway;
  }

  @NonNull
  @Override
  public OrderRouteGateway getOrderRouteGateway() {
    if (orderRouteGateway == null) {
      orderRouteGateway = new OrderRouteGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return orderRouteGateway;
  }

  @NonNull
  @Override
  public ServerConnectionGateway getServerConnectionGateway() {
    if (serverConnectionGateway == null) {
      serverConnectionGateway = new ServerConnectionGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return serverConnectionGateway;
  }

  @NonNull
  @Override
  public ServerTimeGateway getServerTimeGateway() {
    if (serverTimeGateway == null) {
      serverTimeGateway = new ServerTimeGatewayImpl(
          backendComponent.getPersonalTopicListener()
      );
    }
    return serverTimeGateway;
  }

  @NonNull
  @Override
  public UpdateMessageGateway getUpdateMessageGateway() {
    if (updateMessageGateway == null) {
      updateMessageGateway = new UpdateMessageGatewayImpl(
          backendComponent.getPersonalTopicListener()
      );
    }
    return updateMessageGateway;
  }

  @NonNull
  @Override
  public WaitingForClientGateway getWaitingForClientGateway() {
    if (waitingForClientGateway == null) {
      waitingForClientGateway = new WaitingForClientGatewayImpl(
          backendComponent.getStompClient()
      );
    }
    return waitingForClientGateway;
  }

  @NonNull
  @Override
  public PasswordGateway getPasswordGateway() {
    if (passwordGateway == null) {
      passwordGateway = new PasswordGatewayImpl(
          backendComponent.getApiService()
      );
    }
    return passwordGateway;
  }

  @NonNull
  @Override
  public SmsGateway getSmsGateway() {
    if (smsGateway == null) {
      smsGateway = new SmsGatewayImpl(
          backendComponent.getApiService()
      );
    }
    return smsGateway;
  }

  @NonNull
  @Override
  public HeatMapGateway getHeatMapGateway() {
    if (heatMapGateway == null) {
      heatMapGateway = new HeatMapGatewayImpl(
          backendComponent.getApiService()
      );
    }
    return heatMapGateway;
  }

  @NonNull
  @Override
  public ServicesGateway getServicesGateway() {
    if (servicesGateway == null) {
      servicesGateway = new ServicesGatewayImpl(
          backendComponent.getApiService(),
          new ServiceApiMapper()
      );
    }
    return servicesGateway;
  }

  @NonNull
  @Override
  public LastUsedVehicleGateway getLastUsedVehicleGateway() {
    if (lastUsedVehicleGateway == null) {
      lastUsedVehicleGateway = new LastUsedVehicleGatewayImpl(
          backendComponent.getAppSettingsService()
      );
    }
    return lastUsedVehicleGateway;
  }

  @NonNull
  @Override
  public VehicleOptionsGateway getVehicleOptionsGateway() {
    if (vehicleOptionsGateway == null) {
      vehicleOptionsGateway = new VehicleOptionsGatewayImpl(
          backendComponent.getApiService()
      );
    }
    return vehicleOptionsGateway;
  }

  @NonNull
  @Override
  public VehicleOptionsGateway getCurrentVehicleOptionsGateway() {
    if (currentVehicleOptionsGateway == null) {
      currentVehicleOptionsGateway = new CurrentVehicleOptionsGatewayImpl(
          backendComponent.getApiService()
      );
    }
    return currentVehicleOptionsGateway;
  }

  @NonNull
  @Override
  public VehiclesAndOptionsGateway getVehiclesAndOptionsGateway() {
    if (vehiclesAndOptionsGateway == null) {
      vehiclesAndOptionsGateway = new VehiclesAndOptionsGatewayImpl(
          backendComponent.getApiService(),
          new VehicleOptionApiMapper(),
          new VehicleApiMapper(
              new VehicleOptionApiMapper()
          ),
          new ErrorMapper()
      );
    }
    return vehiclesAndOptionsGateway;
  }

  @NonNull
  @Override
  public VehiclesAndOptionsGateway getSelectedVehiclesAndOptionsGateway() {
    if (selectedVehiclesAndOptionsGateway == null) {
      selectedVehiclesAndOptionsGateway = new SelectedVehicleAndOptionsGatewayImpl(
          backendComponent.getApiService(),
          new VehicleOptionApiMapper(),
          new VehicleApiMapper(
              new VehicleOptionApiMapper()
          ),
          new ErrorMapper()
      );
    }
    return selectedVehiclesAndOptionsGateway;
  }
}
