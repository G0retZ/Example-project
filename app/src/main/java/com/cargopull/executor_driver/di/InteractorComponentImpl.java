package com.cargopull.executor_driver.di;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.LoginValidator;
import com.cargopull.executor_driver.entity.PasswordValidator;
import com.cargopull.executor_driver.entity.PhoneNumberValidator;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.CallToClientUseCase;
import com.cargopull.executor_driver.interactor.CallToClientUseCaseImpl;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCase;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCaseImpl;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.interactor.CancelOrderUseCaseImpl;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentUseCase;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentUseCaseImpl;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCase;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCaseImpl;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCase;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCaseImpl;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCaseImpl;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCaseImpl;
import com.cargopull.executor_driver.interactor.GeoLocationUseCase;
import com.cargopull.executor_driver.interactor.GeoLocationUseCaseImpl;
import com.cargopull.executor_driver.interactor.MemoryDataSharer;
import com.cargopull.executor_driver.interactor.MissedOrderUseCase;
import com.cargopull.executor_driver.interactor.MissedOrderUseCaseImpl;
import com.cargopull.executor_driver.interactor.MovingToClientUseCase;
import com.cargopull.executor_driver.interactor.MovingToClientUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderCostDetailsUseCase;
import com.cargopull.executor_driver.interactor.OrderCostDetailsUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCase;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderFulfillmentTimeUseCase;
import com.cargopull.executor_driver.interactor.OrderFulfillmentTimeUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderRouteUseCase;
import com.cargopull.executor_driver.interactor.OrderRouteUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.interactor.OrderUseCaseImpl;
import com.cargopull.executor_driver.interactor.ServerConnectionUseCase;
import com.cargopull.executor_driver.interactor.ServerConnectionUseCaseImpl;
import com.cargopull.executor_driver.interactor.ServerTimeUseCase;
import com.cargopull.executor_driver.interactor.ServerTimeUseCaseImpl;
import com.cargopull.executor_driver.interactor.UpcomingPreOrderMessagesUseCase;
import com.cargopull.executor_driver.interactor.UpcomingPreOrderMessagesUseCaseImpl;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCase;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCaseImpl;
import com.cargopull.executor_driver.interactor.WaitingForClientUseCase;
import com.cargopull.executor_driver.interactor.WaitingForClientUseCaseImpl;
import com.cargopull.executor_driver.interactor.auth.LoginUseCase;
import com.cargopull.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.cargopull.executor_driver.interactor.auth.PasswordUseCase;
import com.cargopull.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.cargopull.executor_driver.interactor.auth.SmsUseCase;
import com.cargopull.executor_driver.interactor.auth.SmsUseCaseImpl;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCase;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCaseImpl;
import com.cargopull.executor_driver.interactor.services.ServicesUseCase;
import com.cargopull.executor_driver.interactor.services.ServicesUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceSharer;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCaseImpl;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.ErrorReporterImpl;
import com.cargopull.executor_driver.utils.TimeUtils;

class InteractorComponentImpl implements InteractorComponent {

  @NonNull
  private final RepositoryComponent repositoryComponent;
  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final MemoryDataSharer<String> loginSharer;
  @NonNull
  private final MemoryDataSharer<Vehicle> vehicleChoiceSharer;
  @NonNull
  private final TimeUtils timeUtils;
  @Nullable
  private CallToClientUseCase callToClientUseCase;
  @Nullable
  private CancelOrderReasonsUseCase cancelOrderReasonsUseCase;
  @Nullable
  private CancelOrderUseCase cancelOrderUseCase;
  @Nullable
  private ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;
  @Nullable
  private CurrentCostPollingUseCase currentCostPollingUseCase;
  @Nullable
  private ExecutorBalanceUseCase executorBalanceUseCase;
  @Nullable
  private ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase;
  @Nullable
  private ExecutorStateNotOnlineUseCase executorStateExitUseCase;
  @Nullable
  private ExecutorStateUseCase executorStateUseCase;
  @Nullable
  private GeoLocationUseCase geoLocationUseCase;
  @Nullable
  private MissedOrderUseCase missedOrderUseCase;
  @Nullable
  private MovingToClientUseCase movingToClientUseCase;
  @Nullable
  private OrderConfirmationUseCase orderConfirmationUseCase;
  @Nullable
  private OrderConfirmationUseCase preOrderConfirmationUseCase;
  @Nullable
  private OrderCostDetailsUseCase orderCostDetailsUseCase;
  @Nullable
  private OrderCurrentCostUseCase orderCurrentCostUseCase;
  @Nullable
  private OrderFulfillmentTimeUseCase orderFulfillmentTimeUseCase;
  @Nullable
  private OrderRouteUseCase orderRouteUseCase;
  @Nullable
  private OrderUseCase orderUseCase;
  @Nullable
  private OrderUseCase preOrderUseCase;
  @Nullable
  private ServerConnectionUseCase serverConnectionUseCase;
  @Nullable
  private ServerTimeUseCase serverTimeUseCase;
  @Nullable
  private UpdateMessageUseCase updateMessageUseCase;
  @Nullable
  private WaitingForClientUseCase waitingForClientUseCase;
  @Nullable
  private LoginUseCase loginUseCase;
  @Nullable
  private PasswordUseCase passwordUseCase;
  @Nullable
  private SmsUseCase smsUseCase;
  @Nullable
  private HeatMapUseCase heatMapUseCase;
  @Nullable
  private ServicesUseCase servicesUseCase;
  @Nullable
  private SelectedVehicleUseCase selectedVehicleUseCase;
  @Nullable
  private UpcomingPreOrderMessagesUseCase upcomingPreOrderMessagesUseCase;
  @Nullable
  private VehicleChoiceUseCase vehicleChoiceUseCase;
  @Nullable
  private VehicleChoiceUseCase currentVehicleChoiceUseCase;
  @Nullable
  private VehicleOptionsUseCase vehicleOptionsUseCase;
  @Nullable
  private VehicleOptionsUseCase currentVehicleOptionsUseCase;
  @Nullable
  private VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase;
  @Nullable
  private VehiclesAndOptionsUseCase selectedVehiclesAndOptionsUseCase;

  InteractorComponentImpl(@NonNull MemoryDataSharer<String> loginSharer,
      @NonNull TimeUtils timeUtils,
      @NonNull RepositoryComponent repositoryComponent) {
    this.timeUtils = timeUtils;
    this.repositoryComponent = repositoryComponent;
    this.loginSharer = loginSharer;
    errorReporter = new ErrorReporterImpl(loginSharer);
    vehicleChoiceSharer = new VehicleChoiceSharer();
  }

  @NonNull
  @Override
  public CallToClientUseCase getCallToClientUseCase() {
    if (callToClientUseCase == null) {
      callToClientUseCase = new CallToClientUseCaseImpl(
          repositoryComponent.getCallToClientGateway()
      );
    }
    return callToClientUseCase;
  }

  @NonNull
  @Override
  public CancelOrderReasonsUseCase getCancelOrderReasonsUseCase() {
    if (cancelOrderReasonsUseCase == null) {
      cancelOrderReasonsUseCase = new CancelOrderReasonsUseCaseImpl(
          errorReporter,
          repositoryComponent.getCancelOrderReasonsGateway()
      );
    }
    return cancelOrderReasonsUseCase;
  }

  @NonNull
  @Override
  public CancelOrderUseCase getCancelOrderUseCase() {
    if (cancelOrderUseCase == null) {
      cancelOrderUseCase = new CancelOrderUseCaseImpl(
          getCancelOrderReasonsUseCase(),
          errorReporter,
          repositoryComponent.getCancelOrderGateway()
      );
    }
    return cancelOrderUseCase;
  }

  @NonNull
  @Override
  public ConfirmOrderPaymentUseCase getConfirmOrderPaymentUseCase() {
    if (confirmOrderPaymentUseCase == null) {
      confirmOrderPaymentUseCase = new ConfirmOrderPaymentUseCaseImpl(
          repositoryComponent.getConfirmOrderPaymentGateway()
      );
    }
    return confirmOrderPaymentUseCase;
  }

  @NonNull
  @Override
  public CurrentCostPollingUseCase getCurrentCostPollingUseCase() {
    if (currentCostPollingUseCase == null) {
      currentCostPollingUseCase = new CurrentCostPollingUseCaseImpl(
          errorReporter,
          repositoryComponent.getCurrentCostPollingGateway()
      );
    }
    return currentCostPollingUseCase;
  }

  @NonNull
  @Override
  public ExecutorBalanceUseCase getExecutorBalanceUseCase() {
    if (executorBalanceUseCase == null) {
      executorBalanceUseCase = new ExecutorBalanceUseCaseImpl(
          errorReporter,
          repositoryComponent.getExecutorBalanceGateway()
      );
    }
    return executorBalanceUseCase;
  }

  @NonNull
  @Override
  public ExecutorStateNotOnlineUseCase getExecutorStateNotOnlineUseCase() {
    if (executorStateNotOnlineUseCase == null) {
      executorStateNotOnlineUseCase = new ExecutorStateNotOnlineUseCaseImpl(
          errorReporter,
          repositoryComponent.getExecutorStateSwitchGateway(),
          getExecutorStateUseCase(),
          ExecutorState.ONLINE
      );
    }
    return executorStateNotOnlineUseCase;
  }

  @NonNull
  @Override
  public ExecutorStateNotOnlineUseCase getExecutorStateExitUseCase() {
    if (executorStateExitUseCase == null) {
      executorStateExitUseCase = new ExecutorStateNotOnlineUseCaseImpl(
          errorReporter,
          repositoryComponent.getExecutorStateSwitchGateway(),
          getExecutorStateUseCase(),
          ExecutorState.SHIFT_CLOSED,
          ExecutorState.SHIFT_OPENED,
          ExecutorState.ONLINE,
          ExecutorState.DRIVER_ORDER_CONFIRMATION,
          ExecutorState.CLIENT_ORDER_CONFIRMATION,
          ExecutorState.MOVING_TO_CLIENT,
          ExecutorState.WAITING_FOR_CLIENT,
          ExecutorState.ORDER_FULFILLMENT,
          ExecutorState.PAYMENT_CONFIRMATION
      );
    }
    return executorStateExitUseCase;
  }

  @NonNull
  @Override
  public ExecutorStateUseCase getExecutorStateUseCase() {
    if (executorStateUseCase == null) {
      executorStateUseCase = new ExecutorStateUseCaseImpl(
          errorReporter,
          repositoryComponent.getExecutorStateGateway()
      );
    }
    return executorStateUseCase;
  }

  @NonNull
  @Override
  public GeoLocationUseCase getGeoLocationUseCase() {
    if (geoLocationUseCase == null) {
      geoLocationUseCase = new GeoLocationUseCaseImpl(
          repositoryComponent.getGeoLocationGateway(),
          repositoryComponent.getGeoTrackingGateway(),
          getExecutorStateUseCase()
      );
    }
    return geoLocationUseCase;
  }

  @NonNull
  @Override
  public MissedOrderUseCase getMissedOrderUseCase() {
    if (missedOrderUseCase == null) {
      missedOrderUseCase = new MissedOrderUseCaseImpl(
          errorReporter,
          repositoryComponent.getMissedOrderGateway()
      );
    }
    return missedOrderUseCase;
  }

  @NonNull
  @Override
  public MovingToClientUseCase getMovingToClientUseCase() {
    if (movingToClientUseCase == null) {
      movingToClientUseCase = new MovingToClientUseCaseImpl(
          repositoryComponent.getMovingToClientGateway()
      );
    }
    return movingToClientUseCase;
  }

  @NonNull
  @Override
  public OrderConfirmationUseCase getOrderConfirmationUseCase() {
    if (orderConfirmationUseCase == null) {
      orderConfirmationUseCase = new OrderConfirmationUseCaseImpl(
          getOrderUseCase(),
          repositoryComponent.getOrderConfirmationGateway()
      );
    }
    return orderConfirmationUseCase;
  }

  @NonNull
  @Override
  public OrderConfirmationUseCase getPreOrderConfirmationUseCase() {
    if (preOrderConfirmationUseCase == null) {
      preOrderConfirmationUseCase = new OrderConfirmationUseCaseImpl(
          getPreOrderUseCase(),
          repositoryComponent.getPreOrderConfirmationGateway()
      );
    }
    return preOrderConfirmationUseCase;
  }

  @NonNull
  @Override
  public OrderCostDetailsUseCase getOrderCostDetailsUseCase() {
    if (orderCostDetailsUseCase == null) {
      orderCostDetailsUseCase = new OrderCostDetailsUseCaseImpl(
          errorReporter,
          repositoryComponent.getOrderCostDetailsGateway()
      );
    }
    return orderCostDetailsUseCase;
  }

  @NonNull
  @Override
  public OrderCurrentCostUseCase getOrderCurrentCostUseCase() {
    if (orderCurrentCostUseCase == null) {
      orderCurrentCostUseCase = new OrderCurrentCostUseCaseImpl(
          errorReporter,
          getOrderUseCase(),
          repositoryComponent.getOrderCurrentCostGateway()
      );
    }
    return orderCurrentCostUseCase;
  }

  @NonNull
  @Override
  public OrderFulfillmentTimeUseCase getOrderFulfillmentTimeUseCase() {
    if (orderFulfillmentTimeUseCase == null) {
      orderFulfillmentTimeUseCase = new OrderFulfillmentTimeUseCaseImpl(
          getOrderUseCase(),
          timeUtils
      );
    }
    return orderFulfillmentTimeUseCase;
  }

  @NonNull
  @Override
  public OrderRouteUseCase getOrderRouteUseCase() {
    if (orderRouteUseCase == null) {
      orderRouteUseCase = new OrderRouteUseCaseImpl(
          getOrderUseCase(),
          repositoryComponent.getOrderRouteGateway()
      );
    }
    return orderRouteUseCase;
  }

  @NonNull
  @Override
  public OrderUseCase getOrderUseCase() {
    if (orderUseCase == null) {
      orderUseCase = new OrderUseCaseImpl(
          errorReporter,
          repositoryComponent.getOrderGateway()
      );

    }
    return orderUseCase;
  }

  @NonNull
  @Override
  public OrderUseCase getPreOrderUseCase() {
    if (preOrderUseCase == null) {
      preOrderUseCase = new OrderUseCaseImpl(
          errorReporter,
          repositoryComponent.getPreOrderGateway()
      );
    }
    return preOrderUseCase;
  }

  @NonNull
  @Override
  public ServerConnectionUseCase getServerConnectionUseCase() {
    if (serverConnectionUseCase == null) {
      serverConnectionUseCase = new ServerConnectionUseCaseImpl(
          repositoryComponent.getServerConnectionGateway()
      );
    }
    return serverConnectionUseCase;
  }

  @NonNull
  @Override
  public ServerTimeUseCase getServerTimeUseCase() {
    if (serverTimeUseCase == null) {
      serverTimeUseCase = new ServerTimeUseCaseImpl(
          errorReporter,
          repositoryComponent.getServerTimeGateway(),
          timeUtils
      );
    }
    return serverTimeUseCase;
  }

  @NonNull
  @Override
  public UpdateMessageUseCase getUpdateMessageUseCase() {
    if (updateMessageUseCase == null) {
      updateMessageUseCase = new UpdateMessageUseCaseImpl(
          errorReporter,
          repositoryComponent.getUpdateMessageGateway()
      );
    }
    return updateMessageUseCase;
  }

  @NonNull
  @Override
  public WaitingForClientUseCase getWaitingForClientUseCase() {
    if (waitingForClientUseCase == null) {
      waitingForClientUseCase = new WaitingForClientUseCaseImpl(
          repositoryComponent.getWaitingForClientGateway()
      );
    }
    return waitingForClientUseCase;
  }

  @NonNull
  @Override
  public LoginUseCase getLoginUseCase() {
    if (loginUseCase == null) {
      loginUseCase = new LoginUseCaseImpl(
          loginSharer,
          new LoginValidator()
      );
    }
    return loginUseCase;
  }

  @NonNull
  @Override
  public PasswordUseCase getPasswordUseCase() {
    if (passwordUseCase == null) {
      passwordUseCase = new PasswordUseCaseImpl(
          repositoryComponent.getPasswordGateway(),
          loginSharer,
          new PasswordValidator()
      );
    }
    return passwordUseCase;
  }

  @NonNull
  @Override
  public SmsUseCase getSmsUseCase() {
    if (smsUseCase == null) {
      smsUseCase = new SmsUseCaseImpl(
          repositoryComponent.getSmsGateway(),
          loginSharer,
          new PhoneNumberValidator()
      );
    }
    return smsUseCase;
  }

  @NonNull
  @Override
  public HeatMapUseCase getHeatMapUseCase() {
    if (heatMapUseCase == null) {
      heatMapUseCase = new HeatMapUseCaseImpl(
          repositoryComponent.getHeatMapGateway()
      );
    }
    return heatMapUseCase;
  }

  @NonNull
  @Override
  public ServicesUseCase getServicesUseCase() {
    if (servicesUseCase == null) {
      servicesUseCase = new ServicesUseCaseImpl(
          errorReporter,
          repositoryComponent.getServicesGateway()
      );
    }
    return servicesUseCase;
  }

  @NonNull
  @Override
  public SelectedVehicleUseCase getSelectedVehicleUseCase() {
    if (selectedVehicleUseCase == null) {
      selectedVehicleUseCase = new SelectedVehicleUseCaseImpl(
          vehicleChoiceSharer
      );
    }
    return selectedVehicleUseCase;
  }

  @NonNull
  @Override
  public UpcomingPreOrderMessagesUseCase getUpcomingPreOrderMessagesUseCase() {
    if (upcomingPreOrderMessagesUseCase == null) {
      upcomingPreOrderMessagesUseCase = new UpcomingPreOrderMessagesUseCaseImpl(
          errorReporter,
          repositoryComponent.UpcomingPreOrderMessagesGateway()
      );
    }
    return upcomingPreOrderMessagesUseCase;
  }

  @NonNull
  @Override
  public VehicleChoiceUseCase getVehicleChoiceUseCase() {
    if (vehicleChoiceUseCase == null) {
      vehicleChoiceUseCase = new VehicleChoiceUseCaseImpl(
          errorReporter,
          repositoryComponent.getVehiclesAndOptionsGateway(),
          vehicleChoiceSharer
      );
    }
    return vehicleChoiceUseCase;
  }

  @NonNull
  @Override
  public VehicleChoiceUseCase getCurrentVehicleChoiceUseCase() {
    if (currentVehicleChoiceUseCase == null) {
      currentVehicleChoiceUseCase = new VehicleChoiceUseCaseImpl(
          errorReporter,
          repositoryComponent.getSelectedVehiclesAndOptionsGateway(),
          vehicleChoiceSharer
      );
    }
    return currentVehicleChoiceUseCase;
  }

  @NonNull
  @Override
  public VehicleOptionsUseCase getVehicleOptionsUseCase() {
    if (vehicleOptionsUseCase == null) {
      vehicleOptionsUseCase = new VehicleOptionsUseCaseImpl(
          errorReporter,
          repositoryComponent.getVehicleOptionsGateway(),
          vehicleChoiceSharer,
          repositoryComponent.getLastUsedVehicleGateway(),
          repositoryComponent.getVehiclesAndOptionsGateway()
      );
    }
    return vehicleOptionsUseCase;
  }

  @NonNull
  @Override
  public VehicleOptionsUseCase getCurrentVehicleOptionsUseCase() {
    if (currentVehicleOptionsUseCase == null) {
      currentVehicleOptionsUseCase = new VehicleOptionsUseCaseImpl(
          errorReporter,
          repositoryComponent.getCurrentVehicleOptionsGateway(),
          vehicleChoiceSharer,
          repositoryComponent.getLastUsedVehicleGateway(),
          repositoryComponent.getSelectedVehiclesAndOptionsGateway()
      );
    }
    return currentVehicleOptionsUseCase;
  }

  @NonNull
  @Override
  public VehiclesAndOptionsUseCase getVehiclesAndOptionsUseCase() {
    if (vehiclesAndOptionsUseCase == null) {
      vehiclesAndOptionsUseCase = new VehiclesAndOptionsUseCaseImpl(
          errorReporter,
          repositoryComponent.getVehiclesAndOptionsGateway(),
          vehicleChoiceSharer,
          repositoryComponent.getLastUsedVehicleGateway()
      );
    }
    return vehiclesAndOptionsUseCase;
  }

  @NonNull
  @Override
  public VehiclesAndOptionsUseCase getSelectedVehiclesAndOptionsUseCase() {
    if (selectedVehiclesAndOptionsUseCase == null) {
      selectedVehiclesAndOptionsUseCase = new VehiclesAndOptionsUseCaseImpl(
          errorReporter,
          repositoryComponent.getSelectedVehiclesAndOptionsGateway(),
          vehicleChoiceSharer,
          repositoryComponent.getLastUsedVehicleGateway()
      );
    }
    return selectedVehiclesAndOptionsUseCase;
  }
}
