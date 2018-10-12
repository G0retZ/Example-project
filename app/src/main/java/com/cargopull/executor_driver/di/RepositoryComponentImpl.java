package com.cargopull.executor_driver.di;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.CallToClientGatewayImpl;
import com.cargopull.executor_driver.gateway.CancelOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.CancelOrderReasonApiMapper;
import com.cargopull.executor_driver.gateway.CancelOrderReasonsFilter;
import com.cargopull.executor_driver.gateway.CancelledOrderApiMapper;
import com.cargopull.executor_driver.gateway.CancelledOrderFilter;
import com.cargopull.executor_driver.gateway.ConfirmOrderPaymentGatewayImpl;
import com.cargopull.executor_driver.gateway.CurrentCostPollingGatewayImpl;
import com.cargopull.executor_driver.gateway.CurrentCostPollingTimersApiMapper;
import com.cargopull.executor_driver.gateway.CurrentVehicleOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.ExecutorBalanceApiMapper;
import com.cargopull.executor_driver.gateway.ExecutorBalanceFilter;
import com.cargopull.executor_driver.gateway.ExecutorStateApiMapper;
import com.cargopull.executor_driver.gateway.ExecutorStateFilter;
import com.cargopull.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import com.cargopull.executor_driver.gateway.GeoLocationGatewayImpl;
import com.cargopull.executor_driver.gateway.GeoTrackingGatewayImpl;
import com.cargopull.executor_driver.gateway.HeatMapGatewayImpl;
import com.cargopull.executor_driver.gateway.LastUsedVehicleGatewayImpl;
import com.cargopull.executor_driver.gateway.MessagePayloadApiMapper;
import com.cargopull.executor_driver.gateway.MissedOrderFilter;
import com.cargopull.executor_driver.gateway.MovingToClientGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderApiMapper;
import com.cargopull.executor_driver.gateway.OrderConfirmationGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderCostDetailsApiMapper;
import com.cargopull.executor_driver.gateway.OrderCostDetailsFilter;
import com.cargopull.executor_driver.gateway.OrderCurrentCostApiMapper;
import com.cargopull.executor_driver.gateway.OrderCurrentCostFilter;
import com.cargopull.executor_driver.gateway.OrderFilter;
import com.cargopull.executor_driver.gateway.OrderRouteGatewayImpl;
import com.cargopull.executor_driver.gateway.PasswordGatewayImpl;
import com.cargopull.executor_driver.gateway.PreOrderConfirmationGatewayImpl;
import com.cargopull.executor_driver.gateway.PreOrderFilter;
import com.cargopull.executor_driver.gateway.PreOrdersListApiMapper;
import com.cargopull.executor_driver.gateway.PreOrdersListFilter;
import com.cargopull.executor_driver.gateway.RoutePointApiMapper;
import com.cargopull.executor_driver.gateway.SelectedVehicleAndOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.ServerConnectionGatewayImpl;
import com.cargopull.executor_driver.gateway.ServerTimeApiMapper;
import com.cargopull.executor_driver.gateway.ServerTimeFilter;
import com.cargopull.executor_driver.gateway.ServiceApiMapper;
import com.cargopull.executor_driver.gateway.ServicesGatewayImpl;
import com.cargopull.executor_driver.gateway.SmsGatewayImpl;
import com.cargopull.executor_driver.gateway.TopicGatewayImpl;
import com.cargopull.executor_driver.gateway.UpcomingPreOrderApiMapper;
import com.cargopull.executor_driver.gateway.UpcomingPreOrderFilter;
import com.cargopull.executor_driver.gateway.UpdateMessageFilter;
import com.cargopull.executor_driver.gateway.VehicleApiMapper;
import com.cargopull.executor_driver.gateway.VehicleOptionApiMapper;
import com.cargopull.executor_driver.gateway.VehicleOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.VehiclesAndOptionsErrorMapper;
import com.cargopull.executor_driver.gateway.VehiclesAndOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.WaitingForClientGatewayImpl;
import com.cargopull.executor_driver.interactor.CallToClientGateway;
import com.cargopull.executor_driver.interactor.CancelOrderGateway;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentGateway;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.interactor.ExecutorStateSwitchGateway;
import com.cargopull.executor_driver.interactor.GeoLocationGateway;
import com.cargopull.executor_driver.interactor.GeoTrackingGateway;
import com.cargopull.executor_driver.interactor.MovingToClientGateway;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import com.cargopull.executor_driver.interactor.OrderRouteGateway;
import com.cargopull.executor_driver.interactor.ServerConnectionGateway;
import com.cargopull.executor_driver.interactor.WaitingForClientGateway;
import com.cargopull.executor_driver.interactor.auth.PasswordGateway;
import com.cargopull.executor_driver.interactor.auth.SmsGateway;
import com.cargopull.executor_driver.interactor.map.HeatMapGateway;
import com.cargopull.executor_driver.interactor.services.ServicesGateway;
import com.cargopull.executor_driver.interactor.vehicle.LastUsedVehicleGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RepositoryComponentImpl implements RepositoryComponent {

  @NonNull
  private final BackendComponent backendComponent;
  @NonNull
  private final GeolocationCenter geolocationCenter;
  @Nullable
  private CallToClientGateway callToClientGateway;
  @Nullable
  private CommonGateway<String> cancelledOrderMessageGateway;
  @Nullable
  private CommonGateway<Order> cancelledOrderGateway;
  @Nullable
  private CancelOrderGateway cancelOrderGateway;
  @Nullable
  private CommonGateway<List<CancelOrderReason>> cancelOrderReasonsGateway;
  @Nullable
  private ConfirmOrderPaymentGateway confirmOrderPaymentGateway;
  @Nullable
  private CurrentCostPollingGateway currentCostPollingGateway;
  @Nullable
  private CommonGateway<ExecutorBalance> executorBalanceGateway;
  @Nullable
  private CommonGateway<ExecutorState> executorStateGateway;
  @Nullable
  private ExecutorStateSwitchGateway executorStateSwitchGateway;
  @Nullable
  private GeoLocationGateway geoLocationGateway;
  @Nullable
  private GeoTrackingGateway geoTrackingGateway;
  @Nullable
  private CommonGateway<String> missedOrderGateway;
  @Nullable
  private MovingToClientGateway movingToClientGateway;
  @Nullable
  private OrderConfirmationGateway orderConfirmationGateway;
  @Nullable
  private OrderConfirmationGateway preOrderConfirmationGateway;
  @Nullable
  private CommonGateway<OrderCostDetails> orderCostDetailsGateway;
  @Nullable
  private CommonGateway<Long> orderCurrentCostGateway;
  @Nullable
  private CommonGateway<Order> orderGateway;
  @Nullable
  private CommonGateway<Order> preOrderGateway;
  @Nullable
  private OrderRouteGateway orderRouteGateway;
  @Nullable
  private ServerConnectionGateway serverConnectionGateway;
  @Nullable
  private CommonGateway<Long> serverTimeGateway;
  @Nullable
  private CommonGateway<String> updateMessageGateway;
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
  private CommonGateway<String> upcomingPreOrderMessagesGateway;
  @Nullable
  private VehicleOptionsGateway vehicleOptionsGateway;
  @Nullable
  private VehicleOptionsGateway currentVehicleOptionsGateway;
  @Nullable
  private VehiclesAndOptionsGateway vehiclesAndOptionsGateway;
  @Nullable
  private VehiclesAndOptionsGateway selectedVehiclesAndOptionsGateway;
  @Nullable
  private CommonGateway<Set<Order>> preOrdersListGateway;
  @Nullable
  private CommonGateway<Order> upcomingPreOrderGateway;

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
  public CommonGateway<Order> getCancelledOrderGateway() {
    if (cancelledOrderGateway == null) {
      cancelledOrderGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new CancelledOrderFilter(),
          new CancelledOrderApiMapper()
      );
    }
    return cancelledOrderGateway;
  }

  @NonNull
  @Override
  public CommonGateway<String> getCancelledOrderMessageGateway() {
    if (cancelledOrderMessageGateway == null) {
      cancelledOrderMessageGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new CancelledOrderFilter(),
          new MessagePayloadApiMapper()
      );
    }
    return cancelledOrderMessageGateway;
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
  public CommonGateway<List<CancelOrderReason>> getCancelOrderReasonsGateway() {
    if (cancelOrderReasonsGateway == null) {
      cancelOrderReasonsGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new CancelOrderReasonsFilter(),
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
  public CommonGateway<ExecutorBalance> getExecutorBalanceGateway() {
    if (executorBalanceGateway == null) {
      executorBalanceGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new ExecutorBalanceFilter(),
          new ExecutorBalanceApiMapper()
      );
    }
    return executorBalanceGateway;
  }

  @NonNull
  @Override
  public CommonGateway<ExecutorState> getExecutorStateGateway() {
    if (executorStateGateway == null) {
      executorStateGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new ExecutorStateFilter(),
          new ExecutorStateApiMapper(new MessagePayloadApiMapper())
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
  public CommonGateway<String> getMissedOrderGateway() {
    if (missedOrderGateway == null) {
      missedOrderGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new MissedOrderFilter(),
          new MessagePayloadApiMapper()
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
          new OrderCostDetailsFilter(),
          new OrderCostDetailsApiMapper()
      );
    }
    return orderCostDetailsGateway;
  }

  @NonNull
  @Override
  public CommonGateway<Long> getOrderCurrentCostGateway() {
    if (orderCurrentCostGateway == null) {
      orderCurrentCostGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new OrderCurrentCostFilter(),
          new OrderCurrentCostApiMapper()
      );
    }
    return orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public CommonGateway<Order> getOrderGateway() {
    if (orderGateway == null) {
      orderGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new OrderFilter(),
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
  public CommonGateway<Order> getPreOrderGateway() {
    if (preOrderGateway == null) {
      preOrderGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new PreOrderFilter(),
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
  public CommonGateway<Long> getServerTimeGateway() {
    if (serverTimeGateway == null) {
      serverTimeGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new ServerTimeFilter(),
          new ServerTimeApiMapper()
      );
    }
    return serverTimeGateway;
  }

  @NonNull
  @Override
  public CommonGateway<String> getUpdateMessageGateway() {
    if (updateMessageGateway == null) {
      updateMessageGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new UpdateMessageFilter(),
          new MessagePayloadApiMapper()
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
  public CommonGateway<String> getUpcomingPreOrderMessagesGateway() {
    if (upcomingPreOrderMessagesGateway == null) {
      upcomingPreOrderMessagesGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new UpcomingPreOrderFilter(),
          new MessagePayloadApiMapper()
      );
    }
    return upcomingPreOrderMessagesGateway;
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
          new VehiclesAndOptionsErrorMapper()
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
          new VehiclesAndOptionsErrorMapper()
      );
    }
    return selectedVehiclesAndOptionsGateway;
  }

  @NonNull
  @Override
  public CommonGateway<Set<Order>> getPreOrdersSetGateway() {
    if (preOrdersListGateway == null) {
      preOrdersListGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new PreOrdersListFilter(),
          new PreOrdersListApiMapper(
              new VehicleOptionApiMapper(),
              new RoutePointApiMapper()
          ),
          new HashSet<>()
      );
    }
    return preOrdersListGateway;
  }

  @Override
  public CommonGateway<Order> getUpcomingPreOrderGateway() {
    if (upcomingPreOrderGateway == null) {
      upcomingPreOrderGateway = new TopicGatewayImpl<>(
          backendComponent.getPersonalTopicListener(),
          new UpcomingPreOrderFilter(),
          new UpcomingPreOrderApiMapper()
      );
    }
    return upcomingPreOrderGateway;
  }
}
